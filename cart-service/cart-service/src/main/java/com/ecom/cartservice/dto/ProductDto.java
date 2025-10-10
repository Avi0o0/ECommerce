package com.ecom.cartservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {

    // Private constructor to prevent instantiation
    private ProductDto() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static class ProductResponse {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Long categoryId;
        private String categoryName;
        private String sku;
        private String imageUrl;
        private Boolean isActive;
        private LocalDateTime createdAt;

        // Constructors
        public ProductResponse() {}

        public ProductResponse(Long id, String name, String description, BigDecimal price, Long categoryId, 
                             String categoryName, String sku, String imageUrl, Boolean isActive, LocalDateTime createdAt) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.price = price;
            this.categoryId = categoryId;
            this.categoryName = categoryName;
            this.sku = sku;
            this.imageUrl = imageUrl;
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

        public Long getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(Long categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
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
    }
}
