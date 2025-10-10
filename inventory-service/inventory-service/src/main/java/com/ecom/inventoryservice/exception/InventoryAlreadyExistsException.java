package com.ecom.inventoryservice.exception;

public class InventoryAlreadyExistsException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public InventoryAlreadyExistsException(String message) {
        super(message);
    }
    
    public InventoryAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InventoryAlreadyExistsException(Long productId) {
        super("Inventory already exists for product ID: " + productId);
    }
}
