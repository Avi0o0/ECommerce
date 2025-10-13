package com.ecom.inventoryservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.inventoryservice.dto.InventoryRequest;
import com.ecom.inventoryservice.dto.InventoryResponse;
import com.ecom.inventoryservice.service.InventoryService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @PostMapping
    public ResponseEntity<InventoryResponse> createInventory(@Valid @RequestBody InventoryRequest request) {
        logger.info("POST /api/inventory - Creating inventory for product: {}", request.getProductId());
        InventoryResponse inventory = inventoryService.createInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }
    
    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventoryByProductId(@PathVariable Long productId) {
        logger.info("GET /api/inventory/{} - Getting inventory for product", productId);
        InventoryResponse inventory = inventoryService.getInventoryByProductId(productId);
        return ResponseEntity.ok(inventory);
    }
    
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        logger.info("GET /api/inventory - Getting all inventory records");
        List<InventoryResponse> inventories = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventories);
    }
    
    @PutMapping("/{productId}/stock")
    public ResponseEntity<InventoryResponse> updateStock(@PathVariable Long productId, @RequestParam Integer stock) {
        logger.info("PUT /api/inventory/{}/stock - Updating stock to {}", productId, stock);
        InventoryResponse inventory = inventoryService.updateStock(productId, stock);
        return ResponseEntity.ok(inventory);
    }
}