package com.movienetscape.usermanagementservice.service.impl;

import com.movienetscape.usermanagementservice.dto.request.AddressDto;
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
    public Mono<SuccessResponse> registerUser(UserRegistrationRequest request) {
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

    private Mono<SuccessResponse> createUserAccount(UserRegistrationRequest request) {
        User user = createUser(request);

        return accountServiceClient.createAccount(
                        request.getEmail(),
                        request.getFirstname(),
                        request.getLastname(),
                        request.getAddressDto(),
                        request.getProfileImageUrl(),
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

    private SuccessResponse generateVerificationAndPublishEvent(User savedUser, Plan userSelectedPlan) {
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

        AddressDto addressDto = AddressDto.builder()
                .zip(savedUser.getAddress().getZip())
                .state(savedUser.getAddress().getState())
                .city(savedUser.getAddress().getCity())
                .street(savedUser.getAddress().getStreet())
                .build();
        UserDto userDto = UserDto.builder()
                .activePlanName(savedUser.getActivePlanName())
                .profileImageUrl(savedUser.getProfileImageUrl())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .addressDto(addressDto)
                .userId(savedUser.getEmail())
                .build();


        return SuccessResponse.builder()
                .message("User created successfully")
                .data(userDto)
                .build();
    }

    @Override
    public SimpleMessageResponse verifyUser(String token) {
        UserVerification verification = verificationRepository.findByToken(token)
                .orElseThrow(() -> new BadRequestException("The token is invalid"));

        if (!isTokenValid(verification)) {
            throw new TokenExpiredException("Token is expired or already verified");
        }

        User user = userRepository.findById(verification.getUser().getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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


    public Mono<SuccessResponse> updateUser(UserRegistrationRequest request) {
        User user = userRepository.findById(request.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid parameters passed"));

        user.setProfileImageUrl(request.getProfileImageUrl());
        user.setAddress(
                new Address(request.getAddressDto().getStreet(),
                        request.getAddressDto().getCity(),
                        request.getAddressDto().getState(),
                        request.getAddressDto().getZip()));
        user.setLastName(request.getLastname());
        user.setFirstName(request.getFirstname());
        user.setEmail(request.getEmail());
        user.setProfileImageUrl(request.getProfileImageUrl());


        User updatedUser = userRepository.save(user);

        AddressDto addressDto = AddressDto.builder()
                .zip(updatedUser.getAddress().getZip())
                .city(updatedUser.getAddress().getCity())
                .state(updatedUser.getAddress().getState())
                .street(updatedUser.getAddress().getStreet())
                .build();

        UserDto userDto = UserDto.builder()
                .activePlanName(updatedUser.getActivePlanName())
                .userId(updatedUser.getEmail())
                .profileImageUrl(updatedUser.getProfileImageUrl())
                .firstName(updatedUser.getFirstName())
                .lastName(updatedUser.getLastName())
                .addressDto(addressDto)
                .build();


        accountServiceClient.publishUpdatedUser(userDto);

        return Mono.just(
                new SuccessResponse(
                        "User Created Successfully",
                        userDto
                ));
    }


}