package com.ecom.notificationservice.exception;

public class NotificationNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public NotificationNotFoundException(String message) {
        super(message);
    }
    
    public NotificationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}