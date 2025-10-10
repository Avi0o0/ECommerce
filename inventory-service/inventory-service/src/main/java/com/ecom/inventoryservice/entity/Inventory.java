package com.ecom.inventoryservice.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;
    
    @Column(name = "available_stock", nullable = false)
    private Integer availableStock;
    
    @Column(name = "reserved_stock", nullable = false)
    private Integer reservedStock;
    
    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated;
    
    @Version
    @Column(name = "version", nullable = false)
    private Integer version;
    
    // Constructors
    public Inventory() {
        this.lastUpdated = LocalDateTime.now();
        this.availableStock = 0;
        this.reservedStock = 0;
        this.version = 0;
    }
    
    public Inventory(Long productId, Integer initialStock) {
        this();
        this.productId = productId;
        this.availableStock = initialStock;
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
    
    // Business methods
    public void updateLastUpdated() {
        this.lastUpdated = LocalDateTime.now();
    }
    
    public Integer getTotalStock() {
        return availableStock + reservedStock;
    }
    
    public boolean hasAvailableStock(Integer quantity) {
        return availableStock >= quantity;
    }
    
    public boolean hasReservedStock(Integer quantity) {
        return reservedStock >= quantity;
    }
    
    public void reserveStock(Integer quantity) {
        if (!hasAvailableStock(quantity)) {
            throw new IllegalStateException("Insufficient available stock");
        }
        this.availableStock -= quantity;
        this.reservedStock += quantity;
        updateLastUpdated();
    }
    
    public void releaseStock(Integer quantity) {
        if (!hasReservedStock(quantity)) {
            throw new IllegalStateException("Insufficient reserved stock");
        }
        this.reservedStock -= quantity;
        this.availableStock += quantity;
        updateLastUpdated();
    }
    
    public void deductStock(Integer quantity) {
        if (!hasReservedStock(quantity)) {
            throw new IllegalStateException("Insufficient reserved stock");
        }
        this.reservedStock -= quantity;
        updateLastUpdated();
    }
    
    public void adjustStock(Integer delta) {
        this.availableStock += delta;
        if (this.availableStock < 0) {
            this.availableStock = 0;
        }
        updateLastUpdated();
    }
    
    @Override
    public String toString() {
        return "Inventory{" +
                "id=" + id +
                ", productId=" + productId +
                ", availableStock=" + availableStock +
                ", reservedStock=" + reservedStock +
                ", lastUpdated=" + lastUpdated +
                ", version=" + version +
                '}';
    }
}
