package com.ecom.productservice.exception;

public class AvailabilityException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	public AvailabilityException(String message) {
		super(message);
	}

	public AvailabilityException(String message, Throwable cause) {
		super(message, cause);
	}
}
