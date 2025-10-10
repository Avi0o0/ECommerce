package com.ecom.paymentservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentDto {
    
    public static class PaymentRequest {
        @NotNull(message = "Order ID is required")
        @Positive(message = "Order ID must be positive")
        private Long orderId;
        
        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be positive")
        private Long userId;
        
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;
        
        @NotBlank(message = "Payment method is required")
        private String paymentMethod;
        
        private String currency = "USD";
        
        // Constructors
        public PaymentRequest() {}
        
        public PaymentRequest(Long orderId, Long userId, BigDecimal amount, String paymentMethod) {
            this.orderId = orderId;
            this.userId = userId;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
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
        private Long id;
        private Long orderId;
        private Long userId;
        private BigDecimal amount;
        private String paymentMethod;
        private String paymentStatus;
        private String transactionId;
        private String currency;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // Constructors
        public PaymentResponse() {}
        
        public PaymentResponse(Long id, Long orderId, Long userId, BigDecimal amount, 
                             String paymentMethod, String paymentStatus, String transactionId, 
                             String currency, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.orderId = orderId;
            this.userId = userId;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
            this.paymentStatus = paymentStatus;
            this.transactionId = transactionId;
            this.currency = currency;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
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
        
        public String getPaymentStatus() {
            return paymentStatus;
        }
        
        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
        
        public String getTransactionId() {
            return transactionId;
        }
        
        public void setTransactionId(String transactionId) {
            this.transactionId = transactionId;
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
    
    public static class PaymentStatusUpdateRequest {
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
    }
    
    public static class RefundRequest {
        @NotNull(message = "Payment ID is required")
        @Positive(message = "Payment ID must be positive")
        private Long paymentId;
        
        @NotNull(message = "Refund amount is required")
        @DecimalMin(value = "0.01", message = "Refund amount must be greater than 0")
        private BigDecimal refundAmount;
        
        private String reason;
        
        // Constructors
        public RefundRequest() {}
        
        public RefundRequest(Long paymentId, BigDecimal refundAmount, String reason) {
            this.paymentId = paymentId;
            this.refundAmount = refundAmount;
            this.reason = reason;
        }
        
        // Getters and Setters
        public Long getPaymentId() {
            return paymentId;
        }
        
        public void setPaymentId(Long paymentId) {
            this.paymentId = paymentId;
        }
        
        public BigDecimal getRefundAmount() {
            return refundAmount;
        }
        
        public void setRefundAmount(BigDecimal refundAmount) {
            this.refundAmount = refundAmount;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}
