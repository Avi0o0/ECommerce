package com.ecom.inventoryservice.dto;

import java.time.LocalDateTime;

public class InventoryResponse {
    
    private Long id;
    private Long productId;
    private Integer availableStock;
    private Integer reservedStock;
    private LocalDateTime lastUpdated;
    private Integer version;
    
    // Constructors
    public InventoryResponse() {}
    
    public InventoryResponse(Long id, Long productId, Integer availableStock, Integer reservedStock, 
                           LocalDateTime lastUpdated, Integer version) {
        this.id = id;
        this.productId = productId;
        this.availableStock = availableStock;
        this.reservedStock = reservedStock;
        this.lastUpdated = lastUpdated;
        this.version = version;
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
    
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
}
