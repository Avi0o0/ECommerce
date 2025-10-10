package com.ecom.orderservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {
    
    public static class PaymentRequest {
        private Long orderId;
        private Long userId;
        private BigDecimal amount;
        private String paymentMethod;
        private String currency;
        
        // Constructors
        public PaymentRequest() {}
        
        public PaymentRequest(Long orderId, Long userId, BigDecimal amount, String paymentMethod, String currency) {
            this.orderId = orderId;
            this.userId = userId;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
            this.currency = currency;
        }
        
        // Getters and Setters
        public Long getOrderId() {
            return orderId;
        }
        
        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        
        public String getPaymentMethod() {
            return paymentMethod;
        }
        
        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
        
        public String getCurrency() {
            return currency;
        }
        
        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }
    
    public static class PaymentResponse {
        private String paymentId;
        private Long orderId;
        private String status;
        private BigDecimal amount;
        private String paymentMethod;
        private String currency;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // Constructors
        public PaymentResponse() {}
        
        // Getters and Setters
        public String getPaymentId() {
            return paymentId;
        }
        
        public void setPaymentId(String paymentId) {
            this.paymentId = paymentId;
        }
        
        public Long getOrderId() {
            return orderId;
        }
        
        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public BigDecimal getAmount() {
            return amount;
        }
        
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }
        
        public String getPaymentMethod() {
            return paymentMethod;
        }
        
        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }
        
        public String getCurrency() {
            return currency;
        }
        
        public void setCurrency(String currency) {
            this.currency = currency;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
