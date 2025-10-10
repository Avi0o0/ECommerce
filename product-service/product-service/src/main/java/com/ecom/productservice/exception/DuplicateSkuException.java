package com.ecom.productservice.exception;

public class DuplicateSkuException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DuplicateSkuException(String message) {
        super(message);
    }

    public DuplicateSkuException(String message, Throwable cause) {
        super(message, cause);
    }
}
