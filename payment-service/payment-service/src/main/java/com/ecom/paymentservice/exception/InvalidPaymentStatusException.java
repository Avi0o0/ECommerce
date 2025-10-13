package com.ecom.paymentservice.exception;

public class InvalidPaymentStatusException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidPaymentStatusException(String message) {
        super(message);
    }
    
    public InvalidPaymentStatusException(String currentStatus, String newStatus) {
        super("Invalid status transition from " + currentStatus + " to " + newStatus);
    }
    
    public InvalidPaymentStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
