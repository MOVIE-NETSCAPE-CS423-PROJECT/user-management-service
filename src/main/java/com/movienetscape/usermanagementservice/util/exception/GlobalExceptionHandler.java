package com.movienetscape.usermanagementservice.util.exception;


import com.movienetscape.usermanagementservice.dto.response.ErrorResponse;
import com.movienetscape.usermanagementservice.dto.response.SimpleMessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserAccountCreationException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAccountCreationException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(SimpleMessageResponse.builder().message(ex.getMessage()).build()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(SimpleMessageResponse.builder().message(ex.getMessage()).build()));
    }

    @ExceptionHandler(UserRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleUserRegistrationFailure(UserRegistrationException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(new ErrorResponse(SimpleMessageResponse.builder().message(ex.getMessage()).build()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationTokenValidity(TokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(SimpleMessageResponse.builder().message(ex.getMessage()).build()));
    }




}
