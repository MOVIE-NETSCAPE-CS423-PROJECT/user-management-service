package com.movienetscape.usermanagementservice.util.exception;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }
}
