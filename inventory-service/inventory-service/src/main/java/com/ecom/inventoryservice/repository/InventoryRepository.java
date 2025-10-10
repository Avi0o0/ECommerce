package com.ecom.inventoryservice.repository;

import com.ecom.inventoryservice.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    
    /**
     * Find inventory by product ID
     */
    Optional<Inventory> findByProductId(Long productId);
    
    /**
     * Find inventories with low stock (available stock below threshold)
     */
    @Query("SELECT i FROM Inventory i WHERE i.availableStock <= :threshold ORDER BY i.availableStock ASC")
    List<Inventory> findLowStockInventories(@Param("threshold") Integer threshold);
    
    /**
     * Find inventories with zero available stock
     */
    @Query("SELECT i FROM Inventory i WHERE i.availableStock = 0 ORDER BY i.lastUpdated DESC")
    List<Inventory> findOutOfStockInventories();
    
    /**
     * Find inventories with reserved stock
     */
    @Query("SELECT i FROM Inventory i WHERE i.reservedStock > 0 ORDER BY i.reservedStock DESC")
    List<Inventory> findInventoriesWithReservedStock();
    
    /**
     * Find inventories updated after a specific time
     */
    @Query("SELECT i FROM Inventory i WHERE i.lastUpdated > :afterTime ORDER BY i.lastUpdated DESC")
    List<Inventory> findInventoriesUpdatedAfter(@Param("afterTime") LocalDateTime afterTime);
    
    /**
     * Find inventories updated between two times
     */
    @Query("SELECT i FROM Inventory i WHERE i.lastUpdated BETWEEN :startTime AND :endTime ORDER BY i.lastUpdated DESC")
    List<Inventory> findInventoriesUpdatedBetween(@Param("startTime") LocalDateTime startTime, 
                                                @Param("endTime") LocalDateTime endTime);
    
    /**
     * Count inventories with low stock
     */
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.availableStock <= :threshold")
    long countLowStockInventories(@Param("threshold") Integer threshold);
    
    /**
     * Count out of stock inventories
     */
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.availableStock = 0")
    long countOutOfStockInventories();
    
    /**
     * Count inventories with reserved stock
     */
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.reservedStock > 0")
    long countInventoriesWithReservedStock();
    
    /**
     * Calculate total available stock across all products
     */
    @Query("SELECT COALESCE(SUM(i.availableStock), 0) FROM Inventory i")
    long calculateTotalAvailableStock();
    
    /**
     * Calculate total reserved stock across all products
     */
    @Query("SELECT COALESCE(SUM(i.reservedStock), 0) FROM Inventory i")
    long calculateTotalReservedStock();
    
    /**
     * Find inventories by product IDs
     */
    @Query("SELECT i FROM Inventory i WHERE i.productId IN :productIds ORDER BY i.productId")
    List<Inventory> findByProductIdIn(@Param("productIds") List<Long> productIds);
    
    /**
     * Check if inventory exists for product ID
     */
    boolean existsByProductId(Long productId);
    
    /**
     * Delete inventory by product ID
     */
    void deleteByProductId(Long productId);
}
