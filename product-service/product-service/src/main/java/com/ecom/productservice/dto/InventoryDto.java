package com.ecom.productservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Min;

public class InventoryDto {

    public static class InventoryRequest {
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Long productId;
        
        @NotNull(message = "Initial stock is required")
        @Min(value = 0, message = "Initial stock must be non-negative")
        private Integer initialStock;
        
        // Constructors
        public InventoryRequest() {}
        
        public InventoryRequest(Long productId, Integer initialStock) {
            this.productId = productId;
            this.initialStock = initialStock;
        }
        
        // Getters and Setters
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public Integer getInitialStock() {
            return initialStock;
        }
        
        public void setInitialStock(Integer initialStock) {
            this.initialStock = initialStock;
        }
    }
    
    public static class AdjustRequest {
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Long productId;
        
        @NotNull(message = "Delta is required")
        private Integer delta;
        
        @NotNull(message = "Reason is required")
        private String reason;
        
        // Constructors
        public AdjustRequest() {}
        
        public AdjustRequest(Long productId, Integer delta, String reason) {
            this.productId = productId;
            this.delta = delta;
            this.reason = reason;
        }
        
        // Getters and Setters
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public Integer getDelta() {
            return delta;
        }
        
        public void setDelta(Integer delta) {
            this.delta = delta;
        }
        
        public String getReason() {
            return reason;
        }
        
        public void setReason(String reason) {
            this.reason = reason;
        }
    }
}