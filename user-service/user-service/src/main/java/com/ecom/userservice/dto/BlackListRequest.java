package com.ecom.userservice.dto;

public class BlackListRequest {

	private String token;

	public BlackListRequest(String token) {
		super();
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

}
