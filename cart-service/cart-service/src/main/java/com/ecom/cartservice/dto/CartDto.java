package com.ecom.cartservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CartDto {

    public static class AddToCartRequest {
        
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        // Constructors
        public AddToCartRequest() {}

        public AddToCartRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
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
    }

    public static class UpdateQuantityRequest {
        
        @NotNull(message = "Product ID is required")
        private Long productId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        // Constructors
        public UpdateQuantityRequest() {}

        public UpdateQuantityRequest(Long productId, Integer quantity) {
            this.productId = productId;
            this.quantity = quantity;
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
    }

    public static class CartItemResponse {
        private Long id;
        private Long productId;
        private String productName;
        private String productDescription;
        private String productImageUrl;
        private Integer quantity;
        private BigDecimal priceAtAddition;
        private BigDecimal currentPrice;
        private BigDecimal totalPrice;
        private LocalDateTime addedAt;

        // Constructors
        public CartItemResponse() {}

        public CartItemResponse(Long id, Long productId, String productName, String productDescription, 
                              String productImageUrl, Integer quantity, BigDecimal priceAtAddition, 
                              BigDecimal currentPrice, BigDecimal totalPrice, LocalDateTime addedAt) {
            this.id = id;
            this.productId = productId;
            this.productName = productName;
            this.productDescription = productDescription;
            this.productImageUrl = productImageUrl;
            this.quantity = quantity;
            this.priceAtAddition = priceAtAddition;
            this.currentPrice = currentPrice;
            this.totalPrice = totalPrice;
            this.addedAt = addedAt;
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

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }

        public String getProductDescription() {
            return productDescription;
        }

        public void setProductDescription(String productDescription) {
            this.productDescription = productDescription;
        }

        public String getProductImageUrl() {
            return productImageUrl;
        }

        public void setProductImageUrl(String productImageUrl) {
            this.productImageUrl = productImageUrl;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPriceAtAddition() {
            return priceAtAddition;
        }

        public void setPriceAtAddition(BigDecimal priceAtAddition) {
            this.priceAtAddition = priceAtAddition;
        }

        public BigDecimal getCurrentPrice() {
            return currentPrice;
        }

        public void setCurrentPrice(BigDecimal currentPrice) {
            this.currentPrice = currentPrice;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }

        public LocalDateTime getAddedAt() {
            return addedAt;
        }

        public void setAddedAt(LocalDateTime addedAt) {
            this.addedAt = addedAt;
        }
    }

    public static class CartResponse {
        private Long id;
        private Long userId;
        private List<CartItemResponse> items;
        private int totalItems;
        private BigDecimal totalPrice;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        // Constructors
        public CartResponse() {}

        public CartResponse(Long id, Long userId, List<CartItemResponse> items, int totalItems, 
                           BigDecimal totalPrice, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.userId = userId;
            this.items = items;
            this.totalItems = totalItems;
            this.totalPrice = totalPrice;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
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

        public List<CartItemResponse> getItems() {
            return items;
        }

        public void setItems(List<CartItemResponse> items) {
            this.items = items;
        }

        public int getTotalItems() {
            return totalItems;
        }

        public void setTotalItems(int totalItems) {
            this.totalItems = totalItems;
        }

        public BigDecimal getTotalPrice() {
            return totalPrice;
        }

        public void setTotalPrice(BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
        }

        public LocalDateTime getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
