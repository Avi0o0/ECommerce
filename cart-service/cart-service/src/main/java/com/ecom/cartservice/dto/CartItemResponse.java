package com.ecom.cartservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItemResponse {
    
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

    public CartItemResponse(Long id, Long productId, Integer quantity, LocalDateTime addedAt) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.addedAt = addedAt;
        this.productName = "Product " + productId;
        this.productDescription = "";
        this.productImageUrl = "";
        this.priceAtAddition = BigDecimal.ZERO;
        this.currentPrice = BigDecimal.ZERO;
        this.totalPrice = BigDecimal.ZERO;
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
