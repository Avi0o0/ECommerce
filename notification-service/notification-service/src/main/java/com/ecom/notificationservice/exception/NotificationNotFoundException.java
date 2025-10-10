package com.ecom.notificationservice.exception;

public class NotificationNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public NotificationNotFoundException(String message) {
        super(message);
    }
    
    public NotificationNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public NotificationNotFoundException(Long notificationId) {
        super("Notification not found with ID: " + notificationId);
    }
}
