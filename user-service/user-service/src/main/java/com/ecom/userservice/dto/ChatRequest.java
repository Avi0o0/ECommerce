package com.ecom.userservice.dto;

import java.util.UUID;

public class ChatRequest {
	private UUID userId;
	private String message;

	public UUID getUserId() {
		return userId;
	}

	public void setUserId(UUID userId) {
		this.userId = userId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
