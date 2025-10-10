package com.ecom.productservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.productservice.dto.ProductDto;
import com.ecom.productservice.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Get all active products", description = "Retrieve all active products in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved products"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductDto.ProductResponse>> getAllProducts() {
        logger.info("GET /products - Fetching all active products");
        List<ProductDto.ProductResponse> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved product"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductDto.ProductResponse> getProductById(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        logger.info("GET /products/{} - Fetching product by ID", id);
        ProductDto.ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter products", description = "Filter products by category, price range, or search text")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully filtered products"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductDto.ProductResponse>> filterProducts(
            @Parameter(description = "Category ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Minimum price") @RequestParam(required = false) java.math.BigDecimal minPrice,
            @Parameter(description = "Maximum price") @RequestParam(required = false) java.math.BigDecimal maxPrice,
            @Parameter(description = "Search text") @RequestParam(required = false) String searchText) {
        
        logger.info("GET /products/filter - Filtering products with categoryId={}, minPrice={}, maxPrice={}, searchText={}", 
                   categoryId, minPrice, maxPrice, searchText);
        
        ProductDto.ProductFilterRequest filterRequest = new ProductDto.ProductFilterRequest(
            categoryId, minPrice, maxPrice, searchText);
        
        List<ProductDto.ProductResponse> products = productService.filterProducts(filterRequest);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    @Operation(summary = "Create new product", description = "Create a new product (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "SKU already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductDto.ProductResponse> createProduct(
            @Parameter(description = "Product details") @Valid @RequestBody ProductDto.ProductRequest request) {
        logger.info("POST /products - Creating new product with SKU: {}", request.getSku());
        ProductDto.ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Update an existing product (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "409", description = "SKU already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ProductDto.ProductResponse> updateProduct(
            @Parameter(description = "Product ID") @PathVariable Long id,
            @Parameter(description = "Updated product details") @Valid @RequestBody ProductDto.ProductRequest request) {
        logger.info("PUT /products/{} - Updating product", id);
        ProductDto.ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Soft delete a product (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID") @PathVariable Long id) {
        logger.info("DELETE /products/{} - Deleting product", id);
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
