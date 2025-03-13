package com.movienetscape.usermanagementservice.service.impl;

import com.movienetscape.usermanagementservice.dto.request.UserCredentialRegistrationRequest;
import com.movienetscape.usermanagementservice.dto.request.UserRegistrationRequest;
import com.movienetscape.usermanagementservice.dto.response.UserRegistrationResponse;
import com.movienetscape.usermanagementservice.dto.response.UserCreationResponse;
import com.movienetscape.usermanagementservice.messaging.event.UserRegisteredEvent;
import com.movienetscape.usermanagementservice.messaging.event.UserVerifiedEvent;
import com.movienetscape.usermanagementservice.messaging.producer.KafkaEventProducerService;
import com.movienetscape.usermanagementservice.model.User;
import com.movienetscape.usermanagementservice.model.UserVerification;
import com.movienetscape.usermanagementservice.repository.UserRepository;
import com.movienetscape.usermanagementservice.repository.VerificationRepository;
import com.movienetscape.usermanagementservice.service.contract.UserService;
import com.movienetscape.usermanagementservice.util.TokenGenerator;
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
import java.util.UUID;

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

    public Mono<UserRegistrationResponse> registerUser(UserRegistrationRequest request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        return Mono.fromCallable(() -> userRepository.findByEmail(request.getEmail())) // Look for existing user
                .publishOn(Schedulers.boundedElastic()) // Use boundedElastic for blocking operations
                .flatMap(existingUserOpt -> {
                    if (existingUserOpt.isPresent()) {
                        log.warn("User with email {} already exists", request.getEmail());
                        return Mono.error(new UserAccountCreationException("User with this email already exists."));
                    }


                    User user = User.builder()
                            .email(request.getEmail())
                            .firstName(request.getFirstname())
                            .lastName(request.getLastname())
                            .active(false)
                            .verified(false)
                            .build();


                    return accountServiceClient.createAccount(request.getEmail())
                            .doOnSuccess(accountCreationResponse ->
                                    log.info("Account created successfully for user {} with Account ID: {}", request.getEmail(),
                                            accountCreationResponse.getAccountId()))
                            .doOnError(throwable ->
                                    log.error("User registration failed", throwable))  // Log the error instead of throwing
                            .flatMap(accountCreationResponse -> authServiceClient.registerUserCredentials(
                                            new UserCredentialRegistrationRequest(request.getEmail(), request.getPassword()))
                                    .doOnSuccess(v -> log.info("User registration successful at account creation stage for {}", request.getEmail()))
                                    .thenReturn(accountCreationResponse.getAccountId()))
                            .flatMap(accountId -> {

                                user.linkAccount(accountId);
                                user.activateAccount();

                                return Mono.fromCallable(() -> userRepository.save(user));
                            })
                            .map(savedUser -> {

                                String verificationToken = TokenGenerator.generateToken();
                                UserVerification verification = UserVerification.builder()
                                        .user(savedUser)
                                        .token(verificationToken)
                                        .tokenExpirationTime(LocalDateTime.now().plusMinutes(30))
                                        .verified(false)
                                        .build();

                                verificationRepository.save(verification);

                                kafkaEventProducer.publishUserRegisteredEvent(
                                        UserRegisteredEvent.builder().verificationUrl("http://localhost/api/v1/verify?token=" + verificationToken).build());

                                return new UserRegistrationResponse(
                                        "A verification mail has been sent to your email address: " + savedUser.getEmail(), savedUser);
                            });
                });
    }

    public void verifyUser(String token) {

        UserVerification verification = verificationRepository
                .findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));


        if (!(verification.getTokenExpirationTime().isAfter(LocalDateTime.now()) && !verification.isVerified()))
            throw new TokenExpiredException("Token is expired or already verified");


        User user = userRepository.findById(verification.getUser().getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));


        user.setVerified(true);
        userRepository.save(user);


        verificationRepository.delete(verification);


        kafkaEventProducer.publishUserVerifiedEvent(
                UserVerifiedEvent.builder()
                        .userId(user.getEmail())
                        .verified(true)
                        .build());

        log.info("Account {} verified", user.getEmail());
    }


    public Mono<UserCreationResponse> getUserByEmail(String email) {
        log.info("Fetching user details for email: {}", email);

        return Mono.fromCallable(() -> userRepository.findByEmail(email))
                .publishOn(Schedulers.boundedElastic())
                .flatMap(existingUserOpt -> existingUserOpt
                        .map(user -> Mono.just(new UserCreationResponse(
                                user.getEmail(),
                                user.getFirstName(),
                                user.getLastName(),
                                user.getEmail(),
                                null)))
                        .orElseGet(() -> {
                            log.warn("User with email {} not found", email);
                            return Mono.error(new UserNotFoundException("User not found."));
                        }));
    }
}
