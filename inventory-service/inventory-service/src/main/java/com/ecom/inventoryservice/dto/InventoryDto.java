package com.ecom.inventoryservice.dto;

import java.time.LocalDateTime;

public class InventoryDto {
    
    public static class InventoryRequest extends com.ecom.inventoryservice.dto.InventoryRequest {
        public InventoryRequest() {
            super();
        }
        
        public InventoryRequest(Long productId, Integer initialStock) {
            super(productId, initialStock);
        }
    }
    
    public static class InventoryResponse extends com.ecom.inventoryservice.dto.InventoryResponse {
        public InventoryResponse() {
            super();
        }
        
        public InventoryResponse(Long id, Long productId, Integer availableStock, Integer reservedStock, 
                               LocalDateTime lastUpdated, Integer version) {
            super(id, productId, availableStock, reservedStock, lastUpdated, version);
        }
    }
    
    public static class ReserveRequest {
        private Long productId;
        private Integer quantity;
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
        private Long productId;
        private Integer quantity;
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
        private Long productId;
        private Integer quantity;
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
    
    public static class AdjustRequest {
        private Long productId;
        private Integer delta;
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