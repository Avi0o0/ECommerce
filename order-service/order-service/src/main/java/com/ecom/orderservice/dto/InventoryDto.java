package com.ecom.orderservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.NotBlank;

public class InventoryDto {
    
    public static class InventoryResponse {
        private Long productId;
        private Integer availableStock;
        private Integer reservedStock;
        private java.time.LocalDateTime lastUpdated;
        private Integer version;
        
        // Constructors
        public InventoryResponse() {}
        
        public InventoryResponse(Long productId, Integer availableStock, Integer reservedStock, 
                               java.time.LocalDateTime lastUpdated, Integer version) {
            this.productId = productId;
            this.availableStock = availableStock;
            this.reservedStock = reservedStock;
            this.lastUpdated = lastUpdated;
            this.version = version;
        }
        
        // Getters and Setters
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public Integer getAvailableStock() {
            return availableStock;
        }
        
        public void setAvailableStock(Integer availableStock) {
            this.availableStock = availableStock;
        }
        
        public Integer getReservedStock() {
            return reservedStock;
        }
        
        public void setReservedStock(Integer reservedStock) {
            this.reservedStock = reservedStock;
        }
        
        public java.time.LocalDateTime getLastUpdated() {
            return lastUpdated;
        }
        
        public void setLastUpdated(java.time.LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
        }
        
        public Integer getVersion() {
            return version;
        }
        
        public void setVersion(Integer version) {
            this.version = version;
        }
    }
    
    public static class ReserveRequest {
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        @NotBlank(message = "Reference ID is required")
        private String referenceId;
        
        // Constructors
        public ReserveRequest() {}
        
        public ReserveRequest(Long productId, Integer quantity, String referenceId) {
            this.productId = productId;
            this.quantity = quantity;
            this.referenceId = referenceId;
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
        
        public String getReferenceId() {
            return referenceId;
        }
        
        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }
    }
    
    public static class ReleaseRequest {
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        @NotBlank(message = "Reference ID is required")
        private String referenceId;
        
        // Constructors
        public ReleaseRequest() {}
        
        public ReleaseRequest(Long productId, Integer quantity, String referenceId) {
            this.productId = productId;
            this.quantity = quantity;
            this.referenceId = referenceId;
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
        
        public String getReferenceId() {
            return referenceId;
        }
        
        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }
    }
    
    public static class DeductRequest {
        @NotNull(message = "Product ID is required")
        @Positive(message = "Product ID must be positive")
        private Long productId;
        
        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be positive")
        private Integer quantity;
        
        @NotNull(message = "Order ID is required")
        @Positive(message = "Order ID must be positive")
        private Long orderId;
        
        // Constructors
        public DeductRequest() {}
        
        public DeductRequest(Long productId, Integer quantity, Long orderId) {
            this.productId = productId;
            this.quantity = quantity;
            this.orderId = orderId;
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
        
        public Long getOrderId() {
            return orderId;
        }
        
        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }
    }
    
    public static class StockOperationResponse {
        private Long productId;
        private Integer reserved;
        private Integer available;
        private String operation;
        private String referenceId;
        
        // Constructors
        public StockOperationResponse() {}
        
        public StockOperationResponse(Long productId, Integer reserved, Integer available, 
                                    String operation, String referenceId) {
            this.productId = productId;
            this.reserved = reserved;
            this.available = available;
            this.operation = operation;
            this.referenceId = referenceId;
        }
        
        // Getters and Setters
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public Integer getReserved() {
            return reserved;
        }
        
        public void setReserved(Integer reserved) {
            this.reserved = reserved;
        }
        
        public Integer getAvailable() {
            return available;
        }
        
        public void setAvailable(Integer available) {
            this.available = available;
        }
        
        public String getOperation() {
            return operation;
        }
        
        public void setOperation(String operation) {
            this.operation = operation;
        }
        
        public String getReferenceId() {
            return referenceId;
        }
        
        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }
    }
}
