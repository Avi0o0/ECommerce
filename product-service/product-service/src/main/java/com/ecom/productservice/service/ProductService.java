package com.ecom.productservice.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.productservice.constants.ProductServiceConstants;
import com.ecom.productservice.dto.OrderItemRequest;
import com.ecom.productservice.dto.OrderRequest;
import com.ecom.productservice.dto.OrderResponse;
import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.dto.ProductResponse;
import com.ecom.productservice.entity.Product;
import com.ecom.productservice.exception.ProductNotFoundException;
import com.ecom.productservice.repository.ProductRepository;
import com.ecom.productservice.client.OrderServiceClient;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;
    private final OrderServiceClient orderServiceClient;

    public ProductService(ProductRepository productRepository, OrderServiceClient orderServiceClient) {
        this.productRepository = productRepository;
        this.orderServiceClient = orderServiceClient;
    }

    /**
     * Get all products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        logger.info(ProductServiceConstants.LOG_GETTING_ALL_PRODUCTS);
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToResponse).toList();
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        logger.info(ProductServiceConstants.LOG_GETTING_PRODUCT_BY_ID, id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(ProductServiceConstants.PRODUCT_NOT_FOUND_BY_ID_MESSAGE + id));
        return convertToResponse(product);
    }

    /**
     * Create new product or add stock to existing product
     */
    public ProductResponse createProduct(ProductRequest request) {
        logger.info(ProductServiceConstants.LOG_CREATING_NEW_PRODUCT, request.getName(), request.getStockKeepingUnit());
        
        // Check if product with this SKU already exists
        Optional<Product> existingProduct = productRepository.findByStockKeepingUnit(request.getStockKeepingUnit());
        
        if (existingProduct.isPresent()) {
            // Product exists, add stock quantity
            Product product = existingProduct.get();
            int newQuantity = product.getStockQuantity() + request.getStockQuantity();
            product.setStockQuantity(newQuantity);
            
            Product savedProduct = productRepository.save(product);
            logger.info(ProductServiceConstants.LOG_ADDED_STOCK_TO_EXISTING_PRODUCT, 
                request.getStockQuantity(), request.getStockKeepingUnit(), newQuantity);
            
            return convertToResponse(savedProduct);
        } else {
            // New product, create it
            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStockKeepingUnit(request.getStockKeepingUnit());
            product.setStockQuantity(request.getStockQuantity());
            
            Product savedProduct = productRepository.save(product);
            logger.info(ProductServiceConstants.LOG_PRODUCT_CREATED_SUCCESSFULLY, 
                savedProduct.getId(), savedProduct.getStockKeepingUnit());
            
            return convertToResponse(savedProduct);
        }
    }

    /**
     * Update product
     */
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        logger.info(ProductServiceConstants.LOG_UPDATING_PRODUCT, id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(ProductServiceConstants.PRODUCT_NOT_FOUND_BY_ID_MESSAGE + id));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockKeepingUnit(request.getStockKeepingUnit());
        product.setStockQuantity(request.getStockQuantity());
        
        Product savedProduct = productRepository.save(product);
        logger.info(ProductServiceConstants.LOG_PRODUCT_UPDATED_SUCCESSFULLY, savedProduct.getId());
        
        return convertToResponse(savedProduct);
    }

    /**
     * Delete product
     */
    public void deleteProduct(Long id) {
        logger.info(ProductServiceConstants.LOG_DELETING_PRODUCT, id);
        
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(ProductServiceConstants.PRODUCT_NOT_FOUND_BY_ID_MESSAGE + id);
        }
        
        productRepository.deleteById(id);
        logger.info(ProductServiceConstants.LOG_PRODUCT_DELETED_SUCCESSFULLY, id);
    }

    /**
     * Add stock to existing product by SKU
     */
    public ProductResponse addStock(String sku, Integer quantity) {
        logger.info(ProductServiceConstants.LOG_ADDING_STOCK_TO_PRODUCT, quantity, sku);
        
        Product product = productRepository.findByStockKeepingUnit(sku)
                .orElseThrow(() -> new ProductNotFoundException(ProductServiceConstants.PRODUCT_NOT_FOUND_BY_SKU_MESSAGE + sku));
        
        int newQuantity = product.getStockQuantity() + quantity;
        product.setStockQuantity(newQuantity);
        
        Product savedProduct = productRepository.save(product);
        logger.info(ProductServiceConstants.LOG_STOCK_ADDED_SUCCESSFULLY, quantity, sku, newQuantity);
        
        return convertToResponse(savedProduct);
    }

    /**
     * Reduce stock from existing product by SKU
     */
    public ProductResponse reduceStock(String sku, Integer quantity) {
        logger.info(ProductServiceConstants.LOG_REDUCING_STOCK_FROM_PRODUCT, quantity, sku);
        
        Product product = productRepository.findByStockKeepingUnit(sku)
                .orElseThrow(() -> new ProductNotFoundException(ProductServiceConstants.PRODUCT_NOT_FOUND_BY_SKU_MESSAGE + sku));
        
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException(String.format(ProductServiceConstants.INSUFFICIENT_STOCK_MESSAGE, 
                product.getStockQuantity(), quantity));
        }
        
        int newQuantity = product.getStockQuantity() - quantity;
        product.setStockQuantity(newQuantity);
        
        Product savedProduct = productRepository.save(product);
        logger.info(ProductServiceConstants.LOG_STOCK_REDUCED_SUCCESSFULLY, quantity, sku, newQuantity);
        
        return convertToResponse(savedProduct);
    }

    /**
     * Get product by SKU
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        logger.info(ProductServiceConstants.LOG_GETTING_PRODUCT_BY_SKU, sku);
        Product product = productRepository.findByStockKeepingUnit(sku)
                .orElseThrow(() -> new ProductNotFoundException(ProductServiceConstants.PRODUCT_NOT_FOUND_BY_SKU_MESSAGE + sku));
        return convertToResponse(product);
    }

    /**
     * Search products by keyword in name or description
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> searchProducts(String keyword) {
        logger.info("Searching products with keyword: {}", keyword);
        List<Product> products = productRepository.searchProducts(keyword);
        logger.info("Found {} products matching keyword '{}'", products.size(), keyword);
        return products.stream()
                .map(this::convertToResponse)
                .toList();
    }

    /**
     * Reduce stock by product ID (for internal service calls)
     */
    public void reduceStockByProductId(Long productId, Integer quantity) {
        logger.info("Reducing stock for product ID: {} by quantity: {}", productId, quantity);
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(ProductServiceConstants.PRODUCT_NOT_FOUND_BY_ID_MESSAGE + productId));
        
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException(String.format(ProductServiceConstants.INSUFFICIENT_STOCK_MESSAGE, 
                product.getStockQuantity(), quantity));
        }
        
        int newQuantity = product.getStockQuantity() - quantity;
        product.setStockQuantity(newQuantity);
        
        productRepository.save(product);
        logger.info("Stock reduced successfully for product ID: {}, new quantity: {}", productId, newQuantity);
    }

    /**
     * Buy now - purchase product directly
     */
    public OrderResponse buyNow(Long userId, Long productId, Integer quantity, String paymentMethod, String authorization) {
        logger.info("Processing buy now for user: {}, product: {}, quantity: {}", userId, productId, quantity);
        
        // Get product details and check availability
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(ProductServiceConstants.PRODUCT_NOT_FOUND_BY_ID_MESSAGE + productId));
        
        // Check if product has sufficient stock
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException(String.format(ProductServiceConstants.INSUFFICIENT_STOCK_MESSAGE, 
                product.getStockQuantity(), quantity));
        }
        
        // Calculate total amount
        BigDecimal totalAmount = product.getPrice().multiply(BigDecimal.valueOf(quantity));
        
        // Prepare order request
        OrderItemRequest orderItem = new OrderItemRequest(
            productId, quantity, product.getPrice()
        );
        
        OrderRequest orderRequest = new OrderRequest(
            userId, totalAmount, paymentMethod, List.of(orderItem)
        );
        
        // Call Order Service to checkout with authorization header
        OrderResponse orderResponse = orderServiceClient.checkout(orderRequest, authorization);
        
        // Reduce stock after successful order
        if ("COMPLETED".equalsIgnoreCase(orderResponse.getOrderStatus())) {
            reduceStock(product.getStockKeepingUnit(), quantity);
        }
        
        logger.info("Buy now completed for user: {}, order: {}", userId, orderResponse.getId());
        return orderResponse;
    }

    /**
     * Convert Product entity to ProductResponse DTO
     */
    private ProductResponse convertToResponse(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockKeepingUnit(),
            product.getStockQuantity(),
            product.getCreatedAt()
        );
    }
}