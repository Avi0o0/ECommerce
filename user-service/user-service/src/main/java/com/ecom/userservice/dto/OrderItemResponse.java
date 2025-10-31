package com.ecom.userservice.dto;

import java.math.BigDecimal;

public class OrderItemResponse {
    private Long id;
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private com.ecom.userservice.dto.ProductResponse product;

    public OrderItemResponse() {}

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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public com.ecom.userservice.dto.ProductResponse getProduct() {
        return product;
    }

    public void setProduct(com.ecom.userservice.dto.ProductResponse product) {
        this.product = product;
    }
}