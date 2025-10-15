package com.ecom.userservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class PasswordVerificationFailedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public PasswordVerificationFailedException(String message) {
        super(message);
    }
    
    public PasswordVerificationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
