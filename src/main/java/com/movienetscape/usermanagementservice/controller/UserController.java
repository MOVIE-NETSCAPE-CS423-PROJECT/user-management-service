package com.movienetscape.usermanagementservice.controller;

import com.movienetscape.usermanagementservice.dto.request.UserRegistrationRequest;
import com.movienetscape.usermanagementservice.dto.request.VerifyTokenRequest;
import com.movienetscape.usermanagementservice.dto.response.SuccessResponse;
import com.movienetscape.usermanagementservice.dto.response.SimpleMessageResponse;
import com.movienetscape.usermanagementservice.service.contract.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Mono<ResponseEntity<SuccessResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Received user registration request for email: {}", request.getEmail());
        return userService.registerUser(request)
                .map(successResponse -> ResponseEntity.status(HttpStatus.CREATED).body(successResponse));
    }

    @PostMapping("/verify")
    public ResponseEntity<SimpleMessageResponse> verifyUser( @Valid @RequestBody VerifyTokenRequest verifyTokenRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.verifyUser(verifyTokenRequest.getToken()));
    }


    @PostMapping("/forgot-password")
    public ResponseEntity<SimpleMessageResponse> forgotPassword(@Valid @RequestBody VerifyTokenRequest verifyTokenRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(userService.verifyUser(verifyTokenRequest.getToken()));
    }


    @PutMapping("/update-info")
    public Mono<ResponseEntity<SuccessResponse>> updateUser(@Valid @RequestBody UserRegistrationRequest request) {
        return userService.updateUser(request).map((successResponse) -> ResponseEntity.status(HttpStatus.OK).body(successResponse));
    }
}


