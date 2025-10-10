package com.ecom.productservice.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.productservice.client.InventoryServiceClient;
import com.ecom.productservice.client.NotificationServiceClient;
import com.ecom.productservice.dto.InventoryDto;
import com.ecom.productservice.dto.NotificationDto;
import com.ecom.productservice.dto.ProductDto;
import com.ecom.productservice.entity.Category;
import com.ecom.productservice.entity.Product;
import com.ecom.productservice.exception.CategoryNotFoundException;
import com.ecom.productservice.exception.DuplicateSkuException;
import com.ecom.productservice.exception.ProductNotFoundException;
import com.ecom.productservice.repository.CategoryRepository;
import com.ecom.productservice.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found with ID: ";
    private static final String PRODUCT_ID_JSON_KEY = "productId";
    private static final String PRODUCT_CODE_JSON_KEY = "productCode";
    private static final String JSON_PRODUCT_ID_FORMAT = "{\"" + PRODUCT_ID_JSON_KEY + "\":%d,\"" + PRODUCT_CODE_JSON_KEY + "\":\"%s\"}";
    private static final String ADMIN_ROLE = "admin";
    private static final String PRODUCT_PREFIX = "Product '";

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryServiceClient inventoryServiceClient;
    private final NotificationServiceClient notificationServiceClient;

    public ProductService(ProductRepository productRepository, 
                         CategoryRepository categoryRepository,
                         InventoryServiceClient inventoryServiceClient,
                         NotificationServiceClient notificationServiceClient) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.notificationServiceClient = notificationServiceClient;
    }

    /**
     * Get all active products
     */
    @Transactional(readOnly = true)
    public List<ProductDto.ProductResponse> getAllActiveProducts() {
        logger.info("Fetching all active products");
        List<Product> products = productRepository.findByIsActiveTrue();
        return products.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Get product by ID (active only)
     */
    @Transactional(readOnly = true)
    public ProductDto.ProductResponse getProductById(Long id) {
        logger.info("Fetching product with ID: {}", id);
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + id));
        return convertToResponse(product);
    }

    /**
     * Filter products based on criteria
     */
    @Transactional(readOnly = true)
    public List<ProductDto.ProductResponse> filterProducts(ProductDto.ProductFilterRequest filterRequest) {
        logger.info("Filtering products with criteria: {}", filterRequest);
        
        List<Product> products;
        
        if (filterRequest.getCategoryId() != null && filterRequest.getMinPrice() != null && filterRequest.getMaxPrice() != null) {
            // Filter by category and price range
            products = productRepository.findByCategoryAndPriceRange(
                filterRequest.getCategoryId(), 
                filterRequest.getMinPrice(), 
                filterRequest.getMaxPrice()
            );
        } else if (filterRequest.getCategoryId() != null) {
            // Filter by category only
            products = productRepository.findByCategoryIdAndIsActiveTrue(filterRequest.getCategoryId());
        } else if (filterRequest.getMinPrice() != null && filterRequest.getMaxPrice() != null) {
            // Filter by price range only
            products = productRepository.findByPriceRangeAndActive(
                filterRequest.getMinPrice(), 
                filterRequest.getMaxPrice()
            );
        } else if (filterRequest.getSearchText() != null && !filterRequest.getSearchText().trim().isEmpty()) {
            // Search by name
            products = productRepository.findByNameContainingIgnoreCase(filterRequest.getSearchText().trim());
        } else {
            // No filters, return all active products
            products = productRepository.findByIsActiveTrue();
        }
        
        return products.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Create a new product
     */
    public ProductDto.ProductResponse createProduct(ProductDto.ProductRequest request) {
        logger.info("Creating new product with SKU: {}", request.getSku());
        
        // Check if SKU already exists
        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException("Product with SKU " + request.getSku() + " already exists");
        }
        
        // Find category
        Category category = categoryRepository.findByIdAndIsActiveTrue(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + request.getCategoryId()));
        
        // Create product
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setSku(request.getSku());
        product.setImageUrl(request.getImageUrl());
        product.setIsActive(true);
        
        Product savedProduct = productRepository.save(product);
        logger.info("Product created successfully with ID: {}", savedProduct.getId());
        
        // Create inventory record
        try {
            inventoryServiceClient.createInventory(new InventoryDto.InventoryRequest(savedProduct.getId(), 0));
            logger.info("Inventory record created for product ID: {}", savedProduct.getId());
        } catch (Exception e) {
            logger.error("Failed to create inventory record for product ID: {}", savedProduct.getId(), e);
            // Don't fail product creation if inventory service is down, but log the issue
            logger.warn("Product created but inventory record creation failed. Manual intervention may be required for product ID: {}", savedProduct.getId());
        }
        
        // Send notification
        try {
            notificationServiceClient.createNotification(new NotificationDto.NotificationRequest(
                "PRODUCT_CREATED",
                "New Product Added",
                PRODUCT_PREFIX + request.getName() + "' has been added to the catalog",
                ADMIN_ROLE,
                String.format(JSON_PRODUCT_ID_FORMAT, savedProduct.getId(), request.getSku())
            ));
            logger.info("Notification sent for product creation");
        } catch (Exception e) {
            logger.error("Failed to send notification for product creation", e);
            // Don't fail product creation if notification service is down
            logger.warn("Product created but notification sending failed. Product ID: {}", savedProduct.getId());
        }
        
        return convertToResponse(savedProduct);
    }

    /**
     * Update an existing product
     */
    public ProductDto.ProductResponse updateProduct(Long id, ProductDto.ProductRequest request) {
        logger.info("Updating product with ID: {}", id);
        
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + id));
        
        // Check if SKU is being changed and if new SKU already exists
        if (!product.getSku().equals(request.getSku()) && productRepository.existsBySku(request.getSku())) {
            throw new DuplicateSkuException("Product with SKU " + request.getSku() + " already exists");
        }
        
        // Find category
        Category category = categoryRepository.findByIdAndIsActiveTrue(request.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + request.getCategoryId()));
        
        // Update product
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setCategory(category);
        product.setSku(request.getSku());
        product.setImageUrl(request.getImageUrl());
        
        Product savedProduct = productRepository.save(product);
        logger.info("Product updated successfully with ID: {}", savedProduct.getId());
        
        // Send notification
        try {
            notificationServiceClient.createNotification(new NotificationDto.NotificationRequest(
                "PRODUCT_UPDATED",
                "Product Updated",
                PRODUCT_PREFIX + request.getName() + "' has been updated",
                ADMIN_ROLE,
                String.format(JSON_PRODUCT_ID_FORMAT, savedProduct.getId(), request.getSku())
            ));
            logger.info("Notification sent for product update");
        } catch (Exception e) {
            logger.error("Failed to send notification for product update", e);
            // Don't fail product update if notification service is down
            logger.warn("Product updated but notification sending failed. Product ID: {}", savedProduct.getId());
        }
        
        return convertToResponse(savedProduct);
    }

    /**
     * Soft delete a product (mark as inactive)
     */
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        
        Product product = productRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + id));
        
        product.setIsActive(false);
        productRepository.save(product);
        logger.info("Product soft deleted successfully with ID: {}", id);
        
        // Delete inventory record (adjust stock to 0)
        try {
            inventoryServiceClient.adjustStock(new InventoryDto.AdjustRequest(product.getId(), -999999, "Product deleted"));
            logger.info("Inventory record adjusted for product ID: {}", product.getId());
        } catch (Exception e) {
            logger.error("Failed to adjust inventory record for product ID: {}", product.getId(), e);
            // Don't fail product deletion if inventory service is down, but log the issue
            logger.warn("Product deleted but inventory adjustment failed. Manual intervention may be required for product ID: {}", product.getId());
        }
        
        // Send notification
        try {
            notificationServiceClient.createNotification(new NotificationDto.NotificationRequest(
                "PRODUCT_DELETED",
                "Product Deleted",
                PRODUCT_PREFIX + product.getName() + "' has been removed from the catalog",
                ADMIN_ROLE,
                "{\"productId\":" + product.getId() + ",\"sku\":\"" + product.getSku() + "\"}"
            ));
            logger.info("Notification sent for product deletion");
        } catch (Exception e) {
            logger.error("Failed to send notification for product deletion", e);
            // Don't fail product deletion if notification service is down
            logger.warn("Product deleted but notification sending failed. Product ID: {}", product.getId());
        }
    }

    /**
     * Convert Product entity to ProductResponse DTO
     */
    private ProductDto.ProductResponse convertToResponse(Product product) {
        return new ProductDto.ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getCategory().getId(),
            product.getCategory().getName(),
            product.getSku(),
            product.getImageUrl(),
            product.getIsActive(),
            product.getCreatedAt()
        );
    }
}
