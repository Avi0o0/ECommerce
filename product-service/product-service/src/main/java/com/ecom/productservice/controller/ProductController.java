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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.dto.ProductResponse;
import com.ecom.productservice.dto.SuccessResponse;
import com.ecom.productservice.service.AuthenticationService;
import com.ecom.productservice.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
    private final ProductService productService;
    private final AuthenticationService authenticationService;

    public ProductController(ProductService productService, AuthenticationService authenticationService) {
        this.productService = productService;
        this.authenticationService = authenticationService;
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        logger.info("GET /products - Getting all products");
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        logger.info("GET /products/{} - Getting product by ID", id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("POST /products - Creating new product: {}", request.getName());
        
        if (authHeader == null) {
            logger.warn("No authorization header provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), "Authorization header required"));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn("Access denied - user is not admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), "Admin access required"));
        }
        
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("PUT /products/{} - Updating product", id);
        
        if (authHeader == null) {
            logger.warn("No authorization header provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), "Authorization header required"));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn("Access denied - user is not admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), "Admin access required"));
        }
        
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("DELETE /products/{} - Deleting product", id);
        
        if (authHeader == null) {
            logger.warn("No authorization header provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), "Authorization header required"));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn("Access denied - user is not admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), "Admin access required"));
        }
        
        productService.deleteProduct(id);
        SuccessResponse response = new SuccessResponse(HttpStatus.OK.value(), "Product deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        logger.info("GET /products/sku/{} - Getting product by SKU", sku);
        ProductResponse product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{sku}/stock/add")
    public ResponseEntity<?> addStock(@PathVariable String sku,
                                    @RequestParam Integer quantity,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("PUT /products/{}/stock/add - Adding {} units", sku, quantity);
        
        if (authHeader == null) {
            logger.warn("No authorization header provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), "Authorization header required"));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn("Access denied - user is not admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), "Admin access required"));
        }
        
        ProductResponse product = productService.addStock(sku, quantity);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{sku}/stock/reduce")
    public ResponseEntity<?> reduceStock(@PathVariable String sku,
                                       @RequestParam Integer quantity,
                                       @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("PUT /products/{}/stock/reduce - Reducing {} units", sku, quantity);
        
        if (authHeader == null) {
            logger.warn("No authorization header provided");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), "Authorization header required"));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn("Access denied - user is not admin");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), "Admin access required"));
        }
        
        ProductResponse product = productService.reduceStock(sku, quantity);
        return ResponseEntity.ok(product);
    }
}