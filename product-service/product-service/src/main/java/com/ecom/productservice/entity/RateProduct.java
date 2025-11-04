package com.ecom.productservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product_ratings")
public class RateProduct {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "stars_count", nullable = false)
	private Integer starsCount;

	@Column(name = "comment", columnDefinition = "text")
	private String comment;

	@Column(name = "user_id", columnDefinition = "text")
	private String userId;

	@Column(name = "product_id", columnDefinition = "text")
	private Long productId;

	public RateProduct(Integer starsCount, String userID, String comment, Long productID) {
		super();
		this.starsCount = starsCount;
		this.comment = comment;
		this.userId = userID;
		this.productId = productID;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getStarsCount() {
		return starsCount;
	}

	public void setStarsCount(Integer starsCount) {
		this.starsCount = starsCount;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

}
