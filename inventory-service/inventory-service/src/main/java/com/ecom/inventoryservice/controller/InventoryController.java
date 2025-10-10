package com.ecom.inventoryservice.controller;

import com.ecom.inventoryservice.dto.InventoryDto;
import com.ecom.inventoryservice.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@Tag(name = "Inventory Management", description = "APIs for managing inventory stock")
public class InventoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);
    
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @GetMapping
    @Operation(summary = "Get all inventory records", description = "Retrieve all inventory records (Admin usage)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventory records retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InventoryDto.InventoryResponse>> getAllInventory() {
        logger.info("Received request to get all inventory records");
        
        List<InventoryDto.InventoryResponse> inventories = inventoryService.getAllInventory();
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    @GetMapping("/{productId}")
    @Operation(summary = "Get inventory by product ID", description = "Retrieve inventory information for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventory found"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InventoryDto.InventoryResponse> getInventoryByProductId(
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        logger.info("Received request to get inventory for product: {}", productId);
        
        InventoryDto.InventoryResponse inventory = inventoryService.getInventoryByProductId(productId);
        return new ResponseEntity<>(inventory, HttpStatus.OK);
    }
    
    @PostMapping
    @Operation(summary = "Create inventory record", description = "Create inventory record for a product (called by Product-Service)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Inventory created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "409", description = "Inventory already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InventoryDto.InventoryResponse> createInventory(
            @Valid @RequestBody InventoryDto.InventoryRequest inventoryRequest) {
        logger.info("Received request to create inventory for product: {}", inventoryRequest.getProductId());
        
        InventoryDto.InventoryResponse inventory = inventoryService.createInventory(inventoryRequest);
        return new ResponseEntity<>(inventory, HttpStatus.CREATED);
    }
    
    @PutMapping("/reserve")
    @Operation(summary = "Reserve stock", description = "Reserve stock for cart/order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock reserved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "409", description = "Insufficient stock or optimistic locking failure"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InventoryDto.StockOperationResponse> reserveStock(
            @Valid @RequestBody InventoryDto.ReserveRequest reserveRequest) {
        logger.info("Received request to reserve stock for product: {}", reserveRequest.getProductId());
        
        InventoryDto.StockOperationResponse response = inventoryService.reserveStock(reserveRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PutMapping("/release")
    @Operation(summary = "Release reserved stock", description = "Release a prior reservation (cart removed or order failed)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock released successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "409", description = "Insufficient reserved stock or optimistic locking failure"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InventoryDto.StockOperationResponse> releaseStock(
            @Valid @RequestBody InventoryDto.ReleaseRequest releaseRequest) {
        logger.info("Received request to release stock for product: {}", releaseRequest.getProductId());
        
        InventoryDto.StockOperationResponse response = inventoryService.releaseStock(releaseRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PutMapping("/deduct")
    @Operation(summary = "Deduct reserved stock", description = "Deduct reserved stock permanently (on successful payment/checkout)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock deducted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "409", description = "Insufficient reserved stock or optimistic locking failure"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InventoryDto.StockOperationResponse> deductStock(
            @Valid @RequestBody InventoryDto.DeductRequest deductRequest) {
        logger.info("Received request to deduct stock for product: {}", deductRequest.getProductId());
        
        InventoryDto.StockOperationResponse response = inventoryService.deductStock(deductRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PutMapping("/adjust")
    @Operation(summary = "Adjust stock", description = "Admin/manual adjustment of stock (increase/decrease)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock adjusted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "404", description = "Inventory not found"),
        @ApiResponse(responseCode = "409", description = "Optimistic locking failure"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InventoryDto.InventoryResponse> adjustStock(
            @Valid @RequestBody InventoryDto.AdjustRequest adjustRequest) {
        logger.info("Received request to adjust stock for product: {}", adjustRequest.getProductId());
        
        InventoryDto.InventoryResponse response = inventoryService.adjustStock(adjustRequest);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock inventories", description = "Retrieve inventories with stock below threshold")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Low stock inventories retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InventoryDto.InventoryResponse>> getLowStockInventories(
            @Parameter(description = "Stock threshold") @RequestParam(defaultValue = "10") Integer threshold) {
        logger.info("Received request to get low stock inventories with threshold: {}", threshold);
        
        List<InventoryDto.InventoryResponse> inventories = inventoryService.getLowStockInventories(threshold);
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    @GetMapping("/out-of-stock")
    @Operation(summary = "Get out of stock inventories", description = "Retrieve inventories with zero available stock")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Out of stock inventories retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InventoryDto.InventoryResponse>> getOutOfStockInventories() {
        logger.info("Received request to get out of stock inventories");
        
        List<InventoryDto.InventoryResponse> inventories = inventoryService.getOutOfStockInventories();
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    @GetMapping("/reserved")
    @Operation(summary = "Get inventories with reserved stock", description = "Retrieve inventories that have reserved stock")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Inventories with reserved stock retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<InventoryDto.InventoryResponse>> getInventoriesWithReservedStock() {
        logger.info("Received request to get inventories with reserved stock");
        
        List<InventoryDto.InventoryResponse> inventories = inventoryService.getInventoriesWithReservedStock();
        return new ResponseEntity<>(inventories, HttpStatus.OK);
    }
    
    @GetMapping("/stats")
    @Operation(summary = "Get inventory statistics", description = "Retrieve overall inventory statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InventoryService.InventoryStats> getInventoryStats() {
        logger.info("Received request to get inventory statistics");
        
        InventoryService.InventoryStats stats = inventoryService.getInventoryStats();
        return new ResponseEntity<>(stats, HttpStatus.OK);
    }
    
    @GetMapping("/{productId}/available")
    @Operation(summary = "Get available stock", description = "Get available stock for a specific product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Available stock retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Integer> getAvailableStock(
            @Parameter(description = "Product ID") @PathVariable Long productId) {
        logger.info("Received request to get available stock for product: {}", productId);
        
        Integer availableStock = inventoryService.getAvailableStock(productId);
        return new ResponseEntity<>(availableStock, HttpStatus.OK);
    }
    
    @GetMapping("/{productId}/check")
    @Operation(summary = "Check stock availability", description = "Check if product has sufficient stock")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock check completed successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Boolean> hasSufficientStock(
            @Parameter(description = "Product ID") @PathVariable Long productId,
            @Parameter(description = "Required quantity") @RequestParam Integer quantity) {
        logger.info("Received request to check stock availability for product: {} quantity: {}", productId, quantity);
        
        Boolean hasStock = inventoryService.hasSufficientStock(productId, quantity);
        return new ResponseEntity<>(hasStock, HttpStatus.OK);
    }
}
