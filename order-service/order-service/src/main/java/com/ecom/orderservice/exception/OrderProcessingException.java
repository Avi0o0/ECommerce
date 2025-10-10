package com.ecom.orderservice.exception;

public class OrderProcessingException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public OrderProcessingException(String message) {
        super(message);
    }
    
    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
