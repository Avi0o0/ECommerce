package com.ecom.orderservice.exception;

public class OrderNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    public OrderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OrderNotFoundException(Long orderId) {
        super("Order not found with ID: " + orderId);
    }
}
