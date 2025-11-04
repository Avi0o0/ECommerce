package com.ecom.productservice.dto;

public class RateRequest {

	private int starsCount;
	private String comment;
	private String userId;
	private Long productId;

	public int getStarsCount() {
		return starsCount;
	}

	public void setStarsCount(int starsCount) {
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
