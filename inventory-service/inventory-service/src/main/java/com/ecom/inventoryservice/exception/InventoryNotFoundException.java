package com.ecom.inventoryservice.exception;

public class InventoryNotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InventoryNotFoundException(String message) {
        super(message);
    }
    
    public InventoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InventoryNotFoundException(Long productId) {
        super("Inventory not found for product ID: " + productId);
    }
}
