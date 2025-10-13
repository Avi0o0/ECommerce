package com.ecom.cartservice.exception;

public class CartNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public CartNotFoundException(String message) {
        super(message);
    }
    
    public CartNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
