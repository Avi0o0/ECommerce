package com.ecom.cartservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {
    
    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private String stockKeepingUnit;
        private Integer stockQuantity;
        private LocalDateTime createdAt;
        
        // Constructors
        public ProductResponse() {}
        
        public ProductResponse(Long id, String name, String description, BigDecimal price, 
                              String stockKeepingUnit, Integer stockQuantity, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.stockKeepingUnit = stockKeepingUnit;
            this.stockQuantity = stockQuantity;
            this.createdAt = createdAt;
        }
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public BigDecimal getPrice() {
            return price;
        }
        
        public void setPrice(BigDecimal price) {
            this.price = price;
        }
        
        public String getStockKeepingUnit() {
            return stockKeepingUnit;
        }
        
        public void setStockKeepingUnit(String stockKeepingUnit) {
            this.stockKeepingUnit = stockKeepingUnit;
        }
        
        public Integer getStockQuantity() {
            return stockQuantity;
        }
        
        public void setStockQuantity(Integer stockQuantity) {
            this.stockQuantity = stockQuantity;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        // Helper method to check if product is available
        public boolean isAvailable() {
            return stockQuantity != null && stockQuantity > 0;
        }
        
        // Helper method to check if product has sufficient stock
        public boolean hasSufficientStock(Integer requestedQuantity) {
            return stockQuantity != null && stockQuantity >= requestedQuantity;
        }
    }
}
