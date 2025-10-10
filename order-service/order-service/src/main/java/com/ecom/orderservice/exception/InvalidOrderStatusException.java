package com.ecom.orderservice.exception;

public class InvalidOrderStatusException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidOrderStatusException(String message) {
        super(message);
    }
    
    public InvalidOrderStatusException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidOrderStatusException(String currentStatus, String requestedStatus) {
        super(String.format("Invalid status transition from %s to %s", currentStatus, requestedStatus));
    }
}
