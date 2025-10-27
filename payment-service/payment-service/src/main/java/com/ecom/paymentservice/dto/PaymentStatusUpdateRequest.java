package com.ecom.paymentservice.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentStatusUpdateRequest {
    
    @NotBlank(message = "Payment status is required")
    private String paymentStatus;
    
    // Constructors
    public PaymentStatusUpdateRequest() {}
    
    public PaymentStatusUpdateRequest(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    // Getters and Setters
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    @Override
    public String toString() {
        return "PaymentStatusUpdateRequest{" +
                "paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
