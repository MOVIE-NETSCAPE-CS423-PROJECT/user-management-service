package com.movienetscape.usermanagementservice.service.impl;

import com.movienetscape.usermanagementservice.dto.request.UpdateUserRequest;
import com.movienetscape.usermanagementservice.dto.request.UserCredentialRegistrationRequest;
import com.movienetscape.usermanagementservice.dto.request.UserRegistrationRequest;
import com.movienetscape.usermanagementservice.dto.response.*;
import com.movienetscape.usermanagementservice.messaging.event.UserRegisteredEvent;
import com.movienetscape.usermanagementservice.messaging.event.UserVerifiedEvent;
import com.movienetscape.usermanagementservice.messaging.producer.KafkaEventProducerService;
import com.movienetscape.usermanagementservice.model.Address;
import com.movienetscape.usermanagementservice.model.User;
import com.movienetscape.usermanagementservice.model.UserVerification;
import com.movienetscape.usermanagementservice.repository.UserRepository;
import com.movienetscape.usermanagementservice.repository.VerificationRepository;
import com.movienetscape.usermanagementservice.service.contract.UserService;
import com.movienetscape.usermanagementservice.util.TokenGenerator;
import com.movienetscape.usermanagementservice.util.exception.BadRequestException;
import com.movienetscape.usermanagementservice.util.exception.TokenExpiredException;
import com.movienetscape.usermanagementservice.util.exception.UserAccountCreationException;
import com.movienetscape.usermanagementservice.util.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final VerificationRepository verificationRepository;
    private final AccountServiceClient accountServiceClient;
    private final AuthorisationServiceClient authServiceClient;
    private final KafkaEventProducerService kafkaEventProducer;

    @Override
    public Mono<UserRegistrationResponse> registerUser(UserRegistrationRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());




        return Mono.fromCallable(() -> userRepository.findByEmail(request.getEmail()))
                .publishOn(Schedulers.boundedElastic())
                .flatMap(existingUserOpt -> {
                    if (existingUserOpt.isPresent()) {
                        return Mono.error(new UserAccountCreationException("User with this email already exists."));
                    }
                    return createUserAccount(request);
                });
    }

    private Mono<UserRegistrationResponse> createUserAccount(UserRegistrationRequest request) {
        User user = createUser(request);

        return accountServiceClient.createAccount(
                request.getFirstname(),
                        request.getLastname(),
                        request.getEmail(),
                        request.getUserSelectedPlan())
                .doOnSuccess(response -> log.info("Account created for user {} with ID: {}", request.getEmail(), response.getAccountId()))
                .flatMap(response -> authServiceClient.registerUserCredentials(
                                new UserCredentialRegistrationRequest(request.getEmail(), request.getPassword()))
                        .doOnSuccess(v -> log.info("User credentials registered for {}", request.getEmail()))
                        .thenReturn(response.getAccountId()))
                .flatMap(accountId -> saveUserWithAccount(user, accountId))
                .map((savedUser -> generateVerificationAndPublishEvent(savedUser, request.getUserSelectedPlan())));
    }

    private User createUser(UserRegistrationRequest request) {
        return User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .active(false)
                .verified(false)
                .build();
    }

    private Mono<User> saveUserWithAccount(User user, String accountId) {
        user.linkAccount(accountId);
        user.activateAccount();
        return Mono.fromCallable(() -> userRepository.save(user))
                .publishOn(Schedulers.boundedElastic());
    }

    private UserRegistrationResponse generateVerificationAndPublishEvent(User savedUser, Plan userSelectedPlan) {
        String verificationToken = TokenGenerator.generateToken();

        UserVerification verification = UserVerification.builder()
                .user(savedUser)
                .token(verificationToken)
                .tokenExpirationTime(LocalDateTime.now().plusMinutes(10))
                .verified(false)
                .build();

        verificationRepository.save(verification);

        kafkaEventProducer.publishUserRegisteredEvent(
                UserRegisteredEvent.builder()
                        .token(verificationToken)
                        .emailAddress(savedUser.getEmail())
                        .build()
        );

        return new UserRegistrationResponse(
                "A verification mail has been sent to your email address: " + savedUser.getEmail(),
                savedUser, userSelectedPlan
        );
    }

    @Override
    public SimpleMessageResponse verifyUser(String token) {
        UserVerification verification = verificationRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (!isTokenValid(verification)) {
            throw new TokenExpiredException("Token is expired or already verified");
        }

        User user = userRepository.findById(verification.getUser().getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setVerified(true);
        userRepository.save(user);
        verificationRepository.delete(verification);

        kafkaEventProducer.publishUserVerifiedEvent(
                UserVerifiedEvent.builder()
                        .userId(user.getEmail())
                        .verified(true)
                        .build()
        );

        return new SimpleMessageResponse("Account verified");
    }

    private boolean isTokenValid(UserVerification verification) {
        return verification.getTokenExpirationTime().isAfter(LocalDateTime.now()) && !verification.isVerified();
    }


    public Mono<UpdateUserResponse> updateUser(UpdateUserRequest request) {
        User user = userRepository.findById(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid parameters passed"));

        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setAddress(
                new Address(request.getStreet(),
                        request.getCity(),
                        request.getState(),
                        request.getZip()));
        user.setLastName(request.getLastname());
        user.setFirstName(request.getFirstname());
        user.setEmail(request.getEmail());
        user.setProfileImageUrl(request.getProfileImageUrl());

        userRepository.save(user);

        return Mono.just(
                new UpdateUserResponse(
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getAddress().getStreet(),
                        user.getAddress().getCity(),
                        user.getAddress().getState(),
                        user.getAddress().getZip(),
                        user.getProfileImageUrl()
                ));
    }


}