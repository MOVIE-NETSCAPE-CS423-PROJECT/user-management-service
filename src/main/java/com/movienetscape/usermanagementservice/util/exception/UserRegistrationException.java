package com.movienetscape.usermanagementservice.util.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class UserRegistrationException extends RuntimeException {

    private final HttpStatus httpStatus;

    public UserRegistrationException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

}
