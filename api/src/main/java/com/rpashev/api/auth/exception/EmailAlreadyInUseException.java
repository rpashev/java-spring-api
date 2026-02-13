package com.rpashev.api.auth.exception;

import com.rpashev.api.exception.ApiException;
import org.springframework.http.HttpStatus;

public class EmailAlreadyInUseException extends ApiException {

    public EmailAlreadyInUseException() {
        super("Email already in use", HttpStatus.CONFLICT);
    }
}
