package com.ecom.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    
    public static class OrderRequest {
        @NotNull(message = "User ID is required")
        @Positive(message = "User ID must be positive")
        private Long userId;
        
        @NotNull(message = "Order items are required")
        private List<OrderItemRequest> orderItems;
        
        // Constructors
        public OrderRequest() {}
        
        public OrderRequest(Long userId, List<OrderItemRequest> orderItems) {
            this.userId = userId;
            this.orderItems = orderItems;
        }
        
        // Getters and Setters
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public List<OrderItemRequest> getOrderItems() {
            return orderItems;
        }
        
        public void setOrderItems(List<OrderItemRequest> orderItems) {
            this.orderItems = orderItems;
        }
    }
    
    public static class OrderResponse {
        private Long id;
        private Long userId;
        private BigDecimal totalAmount;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private List<OrderItemResponse> orderItems;
        
        // Constructors
        public OrderResponse() {}
        
        public OrderResponse(Long id, Long userId, BigDecimal totalAmount, String status, 
                           LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderItemResponse> orderItems) {
            this.id = id;
            this.userId = userId;
            this.totalAmount = totalAmount;
            this.status = status;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.orderItems = orderItems;
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
        
        public BigDecimal getTotalAmount() {
            return totalAmount;
        }
        
        public void setTotalAmount(BigDecimal totalAmount) {
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
    
    public static class OrderItemRequest {
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        // Constructors
        public OrderItemRequest() {}
        
        public OrderItemRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
        }
        
        // Getters and Setters
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public Integer getQuantity() {
            return quantity;
        }
        
        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }
    }
    
    public static class OrderItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal totalPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        // Constructors
        public OrderItemResponse() {}
        
        public OrderItemResponse(Long id, Long productId, String productName, Integer quantity, 
                               BigDecimal price, BigDecimal totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
            this.totalPrice = totalPrice;
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
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
        
        public BigDecimal getTotalPrice() {
            return totalPrice;
        }
        
        public void setTotalPrice(BigDecimal totalPrice) {
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
        @NotNull(message = "Status is required")
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
