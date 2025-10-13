package com.ecom.paymentservice.exception;

public class PaymentNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public PaymentNotFoundException(Long paymentId) {
        super("Payment not found with ID: " + paymentId);
    }
    
    public PaymentNotFoundException(String message) {
        super(message);
    }
    
    public PaymentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}