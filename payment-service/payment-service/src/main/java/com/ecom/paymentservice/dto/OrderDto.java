package com.ecom.paymentservice.dto;

import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    
    public static class OrderResponse {
        private Long id;
        private Long userId;
        private String totalAmount;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<OrderItemResponse> orderItems;
        
        // Constructors
        public OrderResponse() {}
        
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
        
        public String getTotalAmount() {
            return totalAmount;
        }
        
        public void setTotalAmount(String totalAmount) {
            this.totalAmount = totalAmount;
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
        
        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
        
        public List<OrderItemResponse> getOrderItems() {
            return orderItems;
        }
        
        public void setOrderItems(List<OrderItemResponse> orderItems) {
            this.orderItems = orderItems;
        }
    }
    
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private String price;
        private String totalPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // Constructors
        public OrderItemResponse() {}
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public String getProductName() {
            return productName;
        }
        
        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
        
        public String getPrice() {
            return price;
        }
        
        public void setPrice(String price) {
            this.price = price;
        }
        
        public String getTotalPrice() {
            return totalPrice;
        }
        
        public void setTotalPrice(String totalPrice) {
            this.totalPrice = totalPrice;
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
    
    public static class OrderStatusUpdateRequest {
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
    }
}
