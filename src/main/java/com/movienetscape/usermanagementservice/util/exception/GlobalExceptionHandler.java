package com.movienetscape.usermanagementservice.util.exception;


import com.movienetscape.usermanagementservice.dto.request.BadRequestField;
import com.movienetscape.usermanagementservice.dto.response.BadRequestFieldResponse;
import com.movienetscape.usermanagementservice.dto.response.Error;
import com.movienetscape.usermanagementservice.dto.response.SimpleMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAccountCreationException.class)
    public ResponseEntity<Error> handleUserAlreadyExists(UserAccountCreationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new Error(SimpleMessageResponse.builder().message(ex.getMessage()).build()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Error> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Error(SimpleMessageResponse.builder().message(ex.getMessage()).build()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BadRequestFieldResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<BadRequestField> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new BadRequestField(error.getField(), error.getDefaultMessage()))
                .collect(Collectors.toList());

        BadRequestFieldResponse badRequestResponse = BadRequestFieldResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Error")
                .errors(errors)
                .build();

        return new ResponseEntity<>(badRequestResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Error> handleBadRequestException(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(SimpleMessageResponse.builder().message(ex.getMessage()).build()));
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<Error> handleUserRegistrationFailure(UserRegistrationException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(new Error(SimpleMessageResponse.builder().message(ex.getMessage()).build()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<Error> handleVerificationTokenValidity(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Error(SimpleMessageResponse.builder().message(ex.getMessage()).build()));
    }




}
