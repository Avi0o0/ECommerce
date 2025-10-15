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

import com.ecom.productservice.constants.ProductServiceConstants;
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
        logger.info(ProductServiceConstants.LOG_GET_ALL_PRODUCTS_REQUEST);
        List<ProductResponse> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        logger.info(ProductServiceConstants.LOG_GET_PRODUCT_BY_ID_REQUEST, id);
        ProductResponse product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductRequest request,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(ProductServiceConstants.LOG_POST_CREATE_PRODUCT_REQUEST, request.getName());
        
        if (authHeader == null) {
            logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
        }
        
        ProductResponse product = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(ProductServiceConstants.LOG_PUT_UPDATE_PRODUCT_REQUEST, id);
        
        if (authHeader == null) {
            logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
        }
        
        ProductResponse product = productService.updateProduct(id, request);
        return ResponseEntity.ok(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id,
                                          @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(ProductServiceConstants.LOG_DELETE_PRODUCT_REQUEST, id);
        
        if (authHeader == null) {
            logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
        }
        
        productService.deleteProduct(id);
        SuccessResponse response = new SuccessResponse(HttpStatus.OK.value(), ProductServiceConstants.PRODUCT_DELETED_SUCCESS_MESSAGE);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
        logger.info(ProductServiceConstants.LOG_GET_PRODUCT_BY_SKU_REQUEST, sku);
        ProductResponse product = productService.getProductBySku(sku);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{sku}/stock/add")
    public ResponseEntity<?> addStock(@PathVariable String sku,
                                    @RequestParam Integer quantity,
                                    @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(ProductServiceConstants.LOG_PUT_ADD_STOCK_REQUEST, sku, quantity);
        
        if (authHeader == null) {
            logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
        }
        
        ProductResponse product = productService.addStock(sku, quantity);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{sku}/stock/reduce")
    public ResponseEntity<?> reduceStock(@PathVariable String sku,
                                       @RequestParam Integer quantity,
                                       @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info(ProductServiceConstants.LOG_PUT_REDUCE_STOCK_REQUEST, sku, quantity);
        
        if (authHeader == null) {
            logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new SuccessResponse(HttpStatus.UNAUTHORIZED.value(), ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
        }
        
        if (!authenticationService.isAdmin(authHeader)) {
            logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new SuccessResponse(HttpStatus.FORBIDDEN.value(), ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
        }
        
        ProductResponse product = productService.reduceStock(sku, quantity);
        return ResponseEntity.ok(product);
    }
}