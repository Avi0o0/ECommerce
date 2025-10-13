package com.ecom.notificationservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class NotificationRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotBlank(message = "Message is required")
    private String message;
    
    @NotBlank(message = "Type is required")
    private String type = "GENERAL";
    
    // Constructors
    public NotificationRequest() {}
    
    public NotificationRequest(Long userId, String message, String type) {
        this.userId = userId;
        this.message = message;
        this.type = type;
    }
    
    // Getters and Setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
}
