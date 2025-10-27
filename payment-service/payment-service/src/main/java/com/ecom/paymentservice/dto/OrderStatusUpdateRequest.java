package com.ecom.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;

public class OrderStatusUpdateRequest {
    
    @NotBlank(message = "Order status is required")
    private String status;
    
    // Constructors
    public OrderStatusUpdateRequest() {}
    
    public OrderStatusUpdateRequest(String status) {
        this.status = status;
    }
    
    // Getters and Setters
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "OrderStatusUpdateRequest{" +
                "status='" + status + '\'' +
                '}';
    }
}
