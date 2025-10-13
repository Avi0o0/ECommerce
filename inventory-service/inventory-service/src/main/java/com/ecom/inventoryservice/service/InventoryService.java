package com.ecom.inventoryservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.inventoryservice.dto.InventoryRequest;
import com.ecom.inventoryservice.dto.InventoryResponse;
import com.ecom.inventoryservice.entity.Inventory;
import com.ecom.inventoryservice.exception.InventoryNotFoundException;
import com.ecom.inventoryservice.repository.InventoryRepository;

@Service
@Transactional
public class InventoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    private static final String INVENTORY_NOT_FOUND_MESSAGE = "Inventory not found for product ID: ";

    private final InventoryRepository inventoryRepository;
    
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    /**
     * Create inventory record for a product
     */
    public InventoryResponse createInventory(InventoryRequest inventoryRequest) {
        logger.info("Creating inventory for product: {} with initial stock: {}", 
                   inventoryRequest.getProductId(), inventoryRequest.getInitialStock());
        
        Inventory inventory = new Inventory(inventoryRequest.getProductId(), inventoryRequest.getInitialStock());
        inventory = inventoryRepository.save(inventory);
        
        logger.info("Inventory created successfully for product: {}", inventoryRequest.getProductId());
        return convertToResponse(inventory);
    }
    
    /**
     * Get inventory by product ID
     */
    @Transactional(readOnly = true)
    public InventoryResponse getInventoryByProductId(Long productId) {
        logger.info("Getting inventory for product: {}", productId);
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(INVENTORY_NOT_FOUND_MESSAGE + productId));
        return convertToResponse(inventory);
    }
    
    /**
     * Get all inventory records
     */
    @Transactional(readOnly = true)
    public List<InventoryResponse> getAllInventory() {
        logger.info("Getting all inventory records");
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Update inventory stock
     */
    public InventoryResponse updateStock(Long productId, Integer newStock) {
        logger.info("Updating stock for product: {} to {}", productId, newStock);
        
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException(INVENTORY_NOT_FOUND_MESSAGE + productId));
        
        inventory.setAvailableStock(newStock);
        inventory = inventoryRepository.save(inventory);
        
        logger.info("Stock updated successfully for product: {}", productId);
        return convertToResponse(inventory);
    }
    
    /**
     * Convert Inventory entity to InventoryResponse DTO
     */
    private InventoryResponse convertToResponse(Inventory inventory) {
        return new InventoryResponse(
            inventory.getId(),
            inventory.getProductId(),
            inventory.getAvailableStock(),
            inventory.getReservedStock(),
            inventory.getLastUpdated(),
            inventory.getVersion()
        );
    }
}