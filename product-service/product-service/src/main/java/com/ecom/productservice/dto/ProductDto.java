package com.ecom.productservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ProductDto {

	// Private constructor to prevent instantiation
	private ProductDto() {
		throw new UnsupportedOperationException("Utility class");
	}

	public static class ProductRequest {

		@NotBlank(message = "Product name is required")
		@Size(max = 200, message = "Product name must not exceed 200 characters")
		private String name;

		@Size(max = 1000, message = "Description must not exceed 1000 characters")
		private String description;

		@NotNull(message = "Price is required")
		@DecimalMin(value = "0.01", message = "Price must be greater than 0")
		private BigDecimal price;

		@NotNull(message = "Category ID is required")
		private Long categoryId;

		@NotBlank(message = "SKU is required")
		@Size(max = 50, message = "SKU must not exceed 50 characters")
		private String sku;

		@Size(max = 500, message = "Image URL must not exceed 500 characters")
		private String imageUrl;

		// Constructors
		public ProductRequest() {
		}

		public ProductRequest(String name, String description, BigDecimal price, Long categoryId, String sku,
				String imageUrl) {
			this.name = name;
			this.description = description;
			this.price = price;
			this.categoryId = categoryId;
			this.sku = sku;
			this.imageUrl = imageUrl;
		}

		// Getters and Setters
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
		public ProductResponse() {
		}

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

	public static class ProductFilterRequest {
		private Long categoryId;
		private BigDecimal minPrice;
		private BigDecimal maxPrice;
		private String searchText;

		// Constructors
		public ProductFilterRequest() {
		}

		public ProductFilterRequest(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, String searchText) {
			this.categoryId = categoryId;
			this.minPrice = minPrice;
			this.maxPrice = maxPrice;
			this.searchText = searchText;
		}

		// Getters and Setters
		public Long getCategoryId() {
			return categoryId;
		}

		public void setCategoryId(Long categoryId) {
			this.categoryId = categoryId;
		}

		public BigDecimal getMinPrice() {
			return minPrice;
		}

		public void setMinPrice(BigDecimal minPrice) {
			this.minPrice = minPrice;
		}

		public BigDecimal getMaxPrice() {
			return maxPrice;
		}

		public void setMaxPrice(BigDecimal maxPrice) {
			this.maxPrice = maxPrice;
		}

		public String getSearchText() {
			return searchText;
		}

		public void setSearchText(String searchText) {
			this.searchText = searchText;
		}
	}
}
