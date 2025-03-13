package com.movienetscape.usermanagementservice.controller;

import com.movienetscape.usermanagementservice.dto.request.UserRegistrationRequest;
import com.movienetscape.usermanagementservice.dto.response.UserRegistrationResponse;
import com.movienetscape.usermanagementservice.dto.response.UserCreationResponse;
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
    public Mono<ResponseEntity<UserRegistrationResponse>> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        log.info("Received user registration request for email: {}", request.getEmail());

        return userService.registerUser(request)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        userService.verifyUser(token);
        return ResponseEntity.status(HttpStatus.OK).body("Verified");
    }

    @GetMapping("/{email}")
    public Mono<ResponseEntity<UserCreationResponse>> getUserByEmail(@PathVariable String email) {
        log.info("Fetching user details for email: {}", email);

        return userService.getUserByEmail(email)
                .map(response -> ResponseEntity.status(HttpStatus.OK).body(response));
    }
}


