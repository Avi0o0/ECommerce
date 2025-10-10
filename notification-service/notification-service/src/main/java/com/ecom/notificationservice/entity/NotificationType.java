package com.ecom.notificationservice.entity;

public enum NotificationType {
    ORDER("Order"),
    PAYMENT("Payment"),
    PROMOTION("Promotion");
    
    private final String displayName;
    
    NotificationType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}
