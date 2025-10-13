package com.ecom.inventoryservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventoryRequest {
    
    @NotNull(message = "Product ID is required")
    private Long productId;
    
    @NotNull(message = "Initial stock is required")
    @Min(value = 0, message = "Stock must be non-negative")
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
