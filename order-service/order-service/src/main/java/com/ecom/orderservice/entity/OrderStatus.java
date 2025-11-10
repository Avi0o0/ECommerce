package com.ecom.orderservice.entity;

public enum OrderStatus {
	PENDING("Pending"), COMPLETED("Completed"), FAILED("Failed"), INCOMPLETE("Incomplete"), PAID("Paid"),
	SHIPPED("Shipped"), DELIVERED("Delivered"), CANCELED("Canceled");

	private final String displayName;

	OrderStatus(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}
}
