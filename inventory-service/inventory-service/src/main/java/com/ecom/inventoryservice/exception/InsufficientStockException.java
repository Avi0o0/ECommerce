package com.ecom.inventoryservice.exception;

public class InsufficientStockException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InsufficientStockException(Long productId, Integer requestedQuantity, Integer availableQuantity) {
        super(String.format("Insufficient stock for product ID %d. Requested: %d, Available: %d", 
                          productId, requestedQuantity, availableQuantity));
    }
}
