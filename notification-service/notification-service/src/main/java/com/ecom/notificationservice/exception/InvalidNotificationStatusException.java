package com.ecom.notificationservice.exception;

public class InvalidNotificationStatusException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidNotificationStatusException(String message) {
        super(message);
    }
    
    public InvalidNotificationStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
