package com.ecom.inventoryservice.service;

import com.ecom.inventoryservice.dto.InventoryDto;
import com.ecom.inventoryservice.entity.Inventory;
import com.ecom.inventoryservice.exception.InsufficientStockException;
import com.ecom.inventoryservice.exception.InventoryNotFoundException;
import com.ecom.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory testInventory;
    private InventoryDto.InventoryRequest inventoryRequest;
    private InventoryDto.ReserveRequest reserveRequest;
    private InventoryDto.ReleaseRequest releaseRequest;
    private InventoryDto.DeductRequest deductRequest;
    private InventoryDto.AdjustRequest adjustRequest;

    @BeforeEach
    void setUp() {
        testInventory = new Inventory(1L, 100);
        testInventory.setId(1L);
        testInventory.setVersion(0);
        testInventory.setLastUpdated(LocalDateTime.now());

        inventoryRequest = new InventoryDto.InventoryRequest(1L, 100);
        reserveRequest = new InventoryDto.ReserveRequest(1L, 5, "cart-123");
        releaseRequest = new InventoryDto.ReleaseRequest(1L, 5, "cart-123");
        deductRequest = new InventoryDto.DeductRequest(1L, 5, 123L);
        adjustRequest = new InventoryDto.AdjustRequest(1L, 50, "Stock replenishment");
    }

    @Test
    void testCreateInventory_Success() {
        // Given
        when(inventoryRepository.existsByProductId(1L)).thenReturn(false);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // When
        InventoryDto.InventoryResponse response = inventoryService.createInventory(inventoryRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals(100, response.getAvailableStock());
        assertEquals(0, response.getReservedStock());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testCreateInventory_AlreadyExists() {
        // Given
        when(inventoryRepository.existsByProductId(1L)).thenReturn(true);

        // When & Then
        assertThrows(com.ecom.inventoryservice.exception.InventoryAlreadyExistsException.class,
                () -> inventoryService.createInventory(inventoryRequest));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testReserveStock_Success() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // When
        InventoryDto.StockOperationResponse response = inventoryService.reserveStock(reserveRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals(5, response.getReserved());
        assertEquals(95, response.getAvailable());
        assertEquals("RESERVE", response.getOperation());
        assertEquals("cart-123", response.getReferenceId());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testReserveStock_InsufficientStock() {
        // Given
        testInventory.setAvailableStock(2); // Less than requested quantity
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));

        // When & Then
        assertThrows(InsufficientStockException.class,
                () -> inventoryService.reserveStock(reserveRequest));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testReserveStock_InventoryNotFound() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(InventoryNotFoundException.class,
                () -> inventoryService.reserveStock(reserveRequest));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testReleaseStock_Success() {
        // Given
        testInventory.setReservedStock(10); // Set some reserved stock
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // When
        InventoryDto.StockOperationResponse response = inventoryService.releaseStock(releaseRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals(5, response.getReserved()); // 10 - 5 = 5
        assertEquals(105, response.getAvailable()); // 100 + 5 = 105
        assertEquals("RELEASE", response.getOperation());
        assertEquals("cart-123", response.getReferenceId());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testReleaseStock_InsufficientReservedStock() {
        // Given
        testInventory.setReservedStock(2); // Less than requested quantity
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));

        // When & Then
        assertThrows(InsufficientStockException.class,
                () -> inventoryService.releaseStock(releaseRequest));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testDeductStock_Success() {
        // Given
        testInventory.setReservedStock(10); // Set some reserved stock
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // When
        InventoryDto.StockOperationResponse response = inventoryService.deductStock(deductRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals(5, response.getReserved()); // 10 - 5 = 5
        assertEquals(100, response.getAvailable()); // Available stock unchanged
        assertEquals("DEDUCT", response.getOperation());
        assertEquals("123", response.getReferenceId());
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testDeductStock_InsufficientReservedStock() {
        // Given
        testInventory.setReservedStock(2); // Less than requested quantity
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));

        // When & Then
        assertThrows(InsufficientStockException.class,
                () -> inventoryService.deductStock(deductRequest));
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testAdjustStock_Success() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // When
        InventoryDto.InventoryResponse response = inventoryService.adjustStock(adjustRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals(150, response.getAvailableStock()); // 100 + 50 = 150
        assertEquals(0, response.getReservedStock()); // Reserved stock unchanged
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testAdjustStock_NegativeAdjustment() {
        // Given
        adjustRequest.setDelta(-30); // Negative adjustment
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // When
        InventoryDto.InventoryResponse response = inventoryService.adjustStock(adjustRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals(70, response.getAvailableStock()); // 100 - 30 = 70
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testAdjustStock_ExcessiveNegativeAdjustment() {
        // Given
        adjustRequest.setDelta(-150); // Excessive negative adjustment
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(testInventory);

        // When
        InventoryDto.InventoryResponse response = inventoryService.adjustStock(adjustRequest);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals(0, response.getAvailableStock()); // Should not go below 0
        verify(inventoryRepository).save(any(Inventory.class));
    }

    @Test
    void testGetInventoryByProductId_Success() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));

        // When
        InventoryDto.InventoryResponse response = inventoryService.getInventoryByProductId(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getProductId());
        assertEquals(100, response.getAvailableStock());
        assertEquals(0, response.getReservedStock());
    }

    @Test
    void testGetInventoryByProductId_NotFound() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(InventoryNotFoundException.class,
                () -> inventoryService.getInventoryByProductId(1L));
    }

    @Test
    void testHasSufficientStock_Success() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));

        // When
        boolean hasStock = inventoryService.hasSufficientStock(1L, 50);

        // Then
        assertTrue(hasStock);
    }

    @Test
    void testHasSufficientStock_Insufficient() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));

        // When
        boolean hasStock = inventoryService.hasSufficientStock(1L, 150);

        // Then
        assertFalse(hasStock);
    }

    @Test
    void testHasSufficientStock_InventoryNotFound() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        // When
        boolean hasStock = inventoryService.hasSufficientStock(1L, 50);

        // Then
        assertFalse(hasStock);
    }

    @Test
    void testGetAvailableStock_Success() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.of(testInventory));

        // When
        Integer availableStock = inventoryService.getAvailableStock(1L);

        // Then
        assertEquals(100, availableStock);
    }

    @Test
    void testGetAvailableStock_InventoryNotFound() {
        // Given
        when(inventoryRepository.findByProductId(1L)).thenReturn(Optional.empty());

        // When
        Integer availableStock = inventoryService.getAvailableStock(1L);

        // Then
        assertEquals(0, availableStock);
    }
}
