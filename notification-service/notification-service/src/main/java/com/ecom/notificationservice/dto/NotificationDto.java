package com.ecom.notificationservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public class NotificationDto {
    
    public static class NotificationRequest {
        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be positive")
        private Long userId;
        
        @NotBlank(message = "Message is required")
        @Size(max = 1000, message = "Message must not exceed 1000 characters")
        private String message;
        
        @NotBlank(message = "Type is required")
        private String type;
        
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
    
    public static class NotificationResponse {
        private Long id;
        private Long userId;
        private String message;
        private String type;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime sentAt;
        private Integer retryCount;
        private Integer maxRetries;
        
        // Constructors
        public NotificationResponse() {}
        
        public NotificationResponse(Long id, Long userId, String message, String type, 
                                  String status, LocalDateTime createdAt, LocalDateTime sentAt, 
                                  Integer retryCount, Integer maxRetries) {
            this.id = id;
            this.userId = userId;
            this.message = message;
            this.type = type;
            this.status = status;
            this.createdAt = createdAt;
            this.sentAt = sentAt;
            this.retryCount = retryCount;
            this.maxRetries = maxRetries;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
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
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        public LocalDateTime getSentAt() {
            return sentAt;
        }
        
        public void setSentAt(LocalDateTime sentAt) {
            this.sentAt = sentAt;
        }
        
        public Integer getRetryCount() {
            return retryCount;
        }
        
        public void setRetryCount(Integer retryCount) {
            this.retryCount = retryCount;
        }
        
        public Integer getMaxRetries() {
            return maxRetries;
        }
        
        public void setMaxRetries(Integer maxRetries) {
            this.maxRetries = maxRetries;
        }
    }
    
    public static class OrderNotificationRequest {
        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be positive")
        private Long userId;
        
        @NotNull(message = "Order ID is required")
        @Positive(message = "Order ID must be positive")
        private Long orderId;
        
        @NotBlank(message = "Order status is required")
        private String orderStatus;
        
        // Constructors
        public OrderNotificationRequest() {}
        
        public OrderNotificationRequest(Long userId, Long orderId, String orderStatus) {
            this.userId = userId;
            this.orderId = orderId;
            this.orderStatus = orderStatus;
        }
        
        // Getters and Setters
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public Long getOrderId() {
            return orderId;
        }
        
        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
        
        public String getOrderStatus() {
            return orderStatus;
        }
        
        public void setOrderStatus(String orderStatus) {
            this.orderStatus = orderStatus;
        }
    }
    
    public static class PaymentNotificationRequest {
        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be positive")
        private Long userId;
        
        @NotNull(message = "Payment ID is required")
        @Positive(message = "Payment ID must be positive")
        private Long paymentId;
        
        @NotBlank(message = "Payment status is required")
        private String paymentStatus;
        
        private String amount;
        
        // Constructors
        public PaymentNotificationRequest() {}
        
        public PaymentNotificationRequest(Long userId, Long paymentId, String paymentStatus, String amount) {
            this.userId = userId;
            this.paymentId = paymentId;
            this.paymentStatus = paymentStatus;
            this.amount = amount;
        }
        
        // Getters and Setters
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public Long getPaymentId() {
            return paymentId;
        }
        
        public void setPaymentId(Long paymentId) {
            this.paymentId = paymentId;
        }
        
        public String getPaymentStatus() {
            return paymentStatus;
        }
        
        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
        }
        
        public String getAmount() {
            return amount;
        }
        
        public void setAmount(String amount) {
            this.amount = amount;
        }
    }
}
