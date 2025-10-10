package com.ecom.notificationservice.exception;

public class InvalidNotificationTypeException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidNotificationTypeException(String message) {
        super(message);
    }
    
    public InvalidNotificationTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
