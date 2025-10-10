package com.ecom.inventoryservice.exception;

public class OptimisticLockingException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public OptimisticLockingException(String message) {
        super(message);
    }
    
    public OptimisticLockingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public OptimisticLockingException(Long productId) {
        super("Optimistic locking failure for product ID: " + productId + ". Please retry the operation.");
    }
}
