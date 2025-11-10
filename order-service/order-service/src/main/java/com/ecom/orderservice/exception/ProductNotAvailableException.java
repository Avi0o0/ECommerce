package com.ecom.orderservice.exception;

public class ProductNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ProductNotAvailableException(String message) {
		super(message);
	}

	public ProductNotAvailableException(String message, Throwable cause) {
		super(message, cause);
	}
}
