package com.ecom.paymentservice.exception;

public class InvalidPaymentStatusException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InvalidPaymentStatusException(String message) {
        super(message);
    }
    
    public InvalidPaymentStatusException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidPaymentStatusException(String currentStatus, String requestedStatus) {
        super(String.format("Invalid status transition from %s to %s", currentStatus, requestedStatus));
    }
}
