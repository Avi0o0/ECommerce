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
        private Boolean isActive;
        private LocalDateTime createdAt;
        private String imageUrl; // For backward compatibility
        
        // Constructors
        public ProductResponse() {}
        
        public ProductResponse(Long id, String name, String description, BigDecimal price, 
                              String stockKeepingUnit, Boolean isActive, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.stockKeepingUnit = stockKeepingUnit;
            this.isActive = isActive;
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
        
        public Boolean getIsActive() {
            return isActive;
        }
        
        public void setIsActive(Boolean isActive) {
            this.isActive = isActive;
        }
        
        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }
        
        public String getImageUrl() {
            return imageUrl;
        }
        
        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}
