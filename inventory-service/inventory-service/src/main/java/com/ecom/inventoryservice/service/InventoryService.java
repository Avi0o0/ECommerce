package com.ecom.inventoryservice.service;

import com.ecom.inventoryservice.dto.InventoryDto;
import com.ecom.inventoryservice.entity.Inventory;
import com.ecom.inventoryservice.exception.*;
import com.ecom.inventoryservice.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);
    
    private final InventoryRepository inventoryRepository;
    
    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }
    
    /**
     * Create inventory record for a product
     */
    public InventoryDto.InventoryResponse createInventory(InventoryDto.InventoryRequest inventoryRequest) {
        logger.info("Creating inventory for product: {} with initial stock: {}", 
                   inventoryRequest.getProductId(), inventoryRequest.getInitialStock());
        
        // Check if inventory already exists
        if (inventoryRepository.existsByProductId(inventoryRequest.getProductId())) {
            throw new InventoryAlreadyExistsException(inventoryRequest.getProductId());
        }
        
        try {
            Inventory inventory = new Inventory(inventoryRequest.getProductId(), inventoryRequest.getInitialStock());
            inventory = inventoryRepository.save(inventory);
            
            logger.info("Inventory created successfully for product: {}", inventoryRequest.getProductId());
            return convertToInventoryResponse(inventory);
            
        } catch (Exception e) {
            logger.error("Error creating inventory for product {}: {}", inventoryRequest.getProductId(), e.getMessage());
            throw new RuntimeException("Failed to create inventory: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get inventory by product ID
     */
    @Transactional(readOnly = true)
    public InventoryDto.InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
            .orElseThrow(() -> new InventoryNotFoundException(productId));
        
        return convertToInventoryResponse(inventory);
    }
    
    /**
     * Get all inventory records
     */
    @Transactional(readOnly = true)
    public List<InventoryDto.InventoryResponse> getAllInventory() {
        List<Inventory> inventories = inventoryRepository.findAll();
        return inventories.stream()
            .map(this::convertToInventoryResponse)
            .toList();
    }
    
    /**
     * Reserve stock for cart/order
     */
    public InventoryDto.StockOperationResponse reserveStock(InventoryDto.ReserveRequest reserveRequest) {
        logger.info("Reserving {} units of product {} for reference: {}", 
                   reserveRequest.getQuantity(), reserveRequest.getProductId(), reserveRequest.getReferenceId());
        
        try {
            Inventory inventory = inventoryRepository.findByProductId(reserveRequest.getProductId())
                .orElseThrow(() -> new InventoryNotFoundException(reserveRequest.getProductId()));
            
            // Check available stock
            if (!inventory.hasAvailableStock(reserveRequest.getQuantity())) {
                throw new InsufficientStockException(
                    reserveRequest.getProductId(), 
                    reserveRequest.getQuantity(), 
                    inventory.getAvailableStock()
                );
            }
            
            // Reserve stock atomically
            inventory.reserveStock(reserveRequest.getQuantity());
            inventory = inventoryRepository.save(inventory);
            
            logger.info("Stock reserved successfully for product: {} - Reserved: {}, Available: {}", 
                       reserveRequest.getProductId(), inventory.getReservedStock(), inventory.getAvailableStock());
            
            return new InventoryDto.StockOperationResponse(
                inventory.getProductId(),
                inventory.getReservedStock(),
                inventory.getAvailableStock(),
                "RESERVE",
                reserveRequest.getReferenceId()
            );
            
        } catch (OptimisticLockingFailureException e) {
            logger.warn("Optimistic locking failure while reserving stock for product: {}", reserveRequest.getProductId());
            throw new OptimisticLockingException(reserveRequest.getProductId());
        }
    }
    
    /**
     * Release reserved stock
     */
    public InventoryDto.StockOperationResponse releaseStock(InventoryDto.ReleaseRequest releaseRequest) {
        logger.info("Releasing {} units of product {} for reference: {}", 
                   releaseRequest.getQuantity(), releaseRequest.getProductId(), releaseRequest.getReferenceId());
        
        try {
            Inventory inventory = inventoryRepository.findByProductId(releaseRequest.getProductId())
                .orElseThrow(() -> new InventoryNotFoundException(releaseRequest.getProductId()));
            
            // Check reserved stock
            if (!inventory.hasReservedStock(releaseRequest.getQuantity())) {
                logger.warn("Insufficient reserved stock for product: {} - Requested: {}, Reserved: {}", 
                           releaseRequest.getProductId(), releaseRequest.getQuantity(), inventory.getReservedStock());
                // For release operations, we might want to be more lenient
                // Just log the warning and proceed with available reserved stock
                if (inventory.getReservedStock() > 0) {
                    releaseRequest.setQuantity(inventory.getReservedStock());
                } else {
                    throw new InsufficientStockException(
                        releaseRequest.getProductId(), 
                        releaseRequest.getQuantity(), 
                        inventory.getReservedStock()
                    );
                }
            }
            
            // Release stock atomically
            inventory.releaseStock(releaseRequest.getQuantity());
            inventory = inventoryRepository.save(inventory);
            
            logger.info("Stock released successfully for product: {} - Reserved: {}, Available: {}", 
                       releaseRequest.getProductId(), inventory.getReservedStock(), inventory.getAvailableStock());
            
            return new InventoryDto.StockOperationResponse(
                inventory.getProductId(),
                inventory.getReservedStock(),
                inventory.getAvailableStock(),
                "RELEASE",
                releaseRequest.getReferenceId()
            );
            
        } catch (OptimisticLockingFailureException e) {
            logger.warn("Optimistic locking failure while releasing stock for product: {}", releaseRequest.getProductId());
            throw new OptimisticLockingException(releaseRequest.getProductId());
        }
    }
    
    /**
     * Deduct reserved stock permanently (on successful payment)
     */
    public InventoryDto.StockOperationResponse deductStock(InventoryDto.DeductRequest deductRequest) {
        logger.info("Deducting {} units of product {} for order: {}", 
                   deductRequest.getQuantity(), deductRequest.getProductId(), deductRequest.getOrderId());
        
        try {
            Inventory inventory = inventoryRepository.findByProductId(deductRequest.getProductId())
                .orElseThrow(() -> new InventoryNotFoundException(deductRequest.getProductId()));
            
            // Check reserved stock
            if (!inventory.hasReservedStock(deductRequest.getQuantity())) {
                throw new InsufficientStockException(
                    deductRequest.getProductId(), 
                    deductRequest.getQuantity(), 
                    inventory.getReservedStock()
                );
            }
            
            // Deduct stock atomically
            inventory.deductStock(deductRequest.getQuantity());
            inventory = inventoryRepository.save(inventory);
            
            logger.info("Stock deducted successfully for product: {} - Reserved: {}, Available: {}", 
                       deductRequest.getProductId(), inventory.getReservedStock(), inventory.getAvailableStock());
            
            return new InventoryDto.StockOperationResponse(
                inventory.getProductId(),
                inventory.getReservedStock(),
                inventory.getAvailableStock(),
                "DEDUCT",
                deductRequest.getOrderId().toString()
            );
            
        } catch (OptimisticLockingFailureException e) {
            logger.warn("Optimistic locking failure while deducting stock for product: {}", deductRequest.getProductId());
            throw new OptimisticLockingException(deductRequest.getProductId());
        }
    }
    
    /**
     * Adjust stock manually (admin operation)
     */
    public InventoryDto.InventoryResponse adjustStock(InventoryDto.AdjustRequest adjustRequest) {
        logger.info("Adjusting stock for product: {} by {} units. Reason: {}", 
                   adjustRequest.getProductId(), adjustRequest.getDelta(), adjustRequest.getReason());
        
        try {
            Inventory inventory = inventoryRepository.findByProductId(adjustRequest.getProductId())
                .orElseThrow(() -> new InventoryNotFoundException(adjustRequest.getProductId()));
            
            // Adjust stock atomically
            inventory.adjustStock(adjustRequest.getDelta());
            inventory = inventoryRepository.save(inventory);
            
            logger.info("Stock adjusted successfully for product: {} - Available: {}, Reserved: {}", 
                       adjustRequest.getProductId(), inventory.getAvailableStock(), inventory.getReservedStock());
            
            return convertToInventoryResponse(inventory);
            
        } catch (OptimisticLockingFailureException e) {
            logger.warn("Optimistic locking failure while adjusting stock for product: {}", adjustRequest.getProductId());
            throw new OptimisticLockingException(adjustRequest.getProductId());
        }
    }
    
    /**
     * Get low stock inventories
     */
    @Transactional(readOnly = true)
    public List<InventoryDto.InventoryResponse> getLowStockInventories(Integer threshold) {
        List<Inventory> inventories = inventoryRepository.findLowStockInventories(threshold);
        return inventories.stream()
            .map(this::convertToInventoryResponse)
            .toList();
    }
    
    /**
     * Get out of stock inventories
     */
    @Transactional(readOnly = true)
    public List<InventoryDto.InventoryResponse> getOutOfStockInventories() {
        List<Inventory> inventories = inventoryRepository.findOutOfStockInventories();
        return inventories.stream()
            .map(this::convertToInventoryResponse)
            .toList();
    }
    
    /**
     * Get inventories with reserved stock
     */
    @Transactional(readOnly = true)
    public List<InventoryDto.InventoryResponse> getInventoriesWithReservedStock() {
        List<Inventory> inventories = inventoryRepository.findInventoriesWithReservedStock();
        return inventories.stream()
            .map(this::convertToInventoryResponse)
            .toList();
    }
    
    /**
     * Get inventory statistics
     */
    @Transactional(readOnly = true)
    public InventoryStats getInventoryStats() {
        long totalAvailableStock = inventoryRepository.calculateTotalAvailableStock();
        long totalReservedStock = inventoryRepository.calculateTotalReservedStock();
        long lowStockCount = inventoryRepository.countLowStockInventories(10); // threshold of 10
        long outOfStockCount = inventoryRepository.countOutOfStockInventories();
        long reservedStockCount = inventoryRepository.countInventoriesWithReservedStock();
        
        return new InventoryStats(
            totalAvailableStock,
            totalReservedStock,
            lowStockCount,
            outOfStockCount,
            reservedStockCount
        );
    }
    
    /**
     * Check if product has sufficient stock
     */
    @Transactional(readOnly = true)
    public boolean hasSufficientStock(Long productId, Integer quantity) {
        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        return inventory.map(inv -> inv.hasAvailableStock(quantity)).orElse(false);
    }
    
    /**
     * Get available stock for a product
     */
    @Transactional(readOnly = true)
    public Integer getAvailableStock(Long productId) {
        Optional<Inventory> inventory = inventoryRepository.findByProductId(productId);
        return inventory.map(Inventory::getAvailableStock).orElse(0);
    }
    
    // Helper methods
    
    private InventoryDto.InventoryResponse convertToInventoryResponse(Inventory inventory) {
        return new InventoryDto.InventoryResponse(
            inventory.getProductId(),
            inventory.getAvailableStock(),
            inventory.getReservedStock(),
            inventory.getLastUpdated(),
            inventory.getVersion()
        );
    }
    
    // Inner class for statistics
    public static class InventoryStats {
        private final long totalAvailableStock;
        private final long totalReservedStock;
        private final long lowStockCount;
        private final long outOfStockCount;
        private final long reservedStockCount;
        
        public InventoryStats(long totalAvailableStock, long totalReservedStock, long lowStockCount, 
                             long outOfStockCount, long reservedStockCount) {
            this.totalAvailableStock = totalAvailableStock;
            this.totalReservedStock = totalReservedStock;
            this.lowStockCount = lowStockCount;
            this.outOfStockCount = outOfStockCount;
            this.reservedStockCount = reservedStockCount;
        }
        
        public long getTotalAvailableStock() {
            return totalAvailableStock;
        }
        
        public long getTotalReservedStock() {
            return totalReservedStock;
        }
        
        public long getLowStockCount() {
            return lowStockCount;
        }
        
        public long getOutOfStockCount() {
            return outOfStockCount;
        }
        
        public long getReservedStockCount() {
            return reservedStockCount;
        }
        
        public long getTotalStock() {
            return totalAvailableStock + totalReservedStock;
        }
    }
}
