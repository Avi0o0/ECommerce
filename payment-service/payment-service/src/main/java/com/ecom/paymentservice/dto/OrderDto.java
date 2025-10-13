package com.ecom.paymentservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderDto {
    
    public static class OrderResponse {
        private Long id;
        private Long userId;
        private BigDecimal totalAmount;
        private String status;
        private LocalDateTime createdAt;
        private List<OrderItemResponse> orderItems;
        
        // Constructors
        public OrderResponse() {}
        
        public OrderResponse(Long id, Long userId, BigDecimal totalAmount, String status, 
                           LocalDateTime createdAt, List<OrderItemResponse> orderItems) {
            this.id = id;
            this.userId = userId;
            this.totalAmount = totalAmount;
            this.status = status;
            this.createdAt = createdAt;
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
        
        public List<OrderItemResponse> getOrderItems() {
            return orderItems;
        }
        
        public void setOrderItems(List<OrderItemResponse> orderItems) {
            this.orderItems = orderItems;
        }
        
        public static class OrderItemResponse {
            private Long id;
            private Long productId;
            private Integer quantity;
            private BigDecimal price;
            
            // Constructors
            public OrderItemResponse() {}
            
            public OrderItemResponse(Long id, Long productId, Integer quantity, BigDecimal price) {
                this.id = id;
                this.productId = productId;
                this.quantity = quantity;
                this.price = price;
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
