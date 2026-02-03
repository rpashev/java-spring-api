package com.rpashev.api.auth.exception;

import com.rpashev.api.exception.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends ApiException {

    public InvalidCredentialsException() {
        super("Invalid credentials", HttpStatus.UNAUTHORIZED);
    }
}
