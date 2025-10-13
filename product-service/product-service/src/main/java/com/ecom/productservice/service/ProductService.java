package com.ecom.productservice.service;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.dto.ProductResponse;
import com.ecom.productservice.entity.Product;
import com.ecom.productservice.exception.ProductNotFoundException;
import com.ecom.productservice.repository.ProductRepository;

@Service
@Transactional
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found with ID: ";

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all products
     */
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        logger.info("Getting all products");
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::convertToResponse).toList();
    }

    /**
     * Get product by ID
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        logger.info("Getting product by ID: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + id));
        return convertToResponse(product);
    }

    /**
     * Create new product or add stock to existing product
     */
    public ProductResponse createProduct(ProductRequest request) {
        logger.info("Creating new product with name: {} and SKU: {}", request.getName(), request.getStockKeepingUnit());
        
        // Check if product with this SKU already exists
        Optional<Product> existingProduct = productRepository.findByStockKeepingUnit(request.getStockKeepingUnit());
        
        if (existingProduct.isPresent()) {
            // Product exists, add stock quantity
            Product product = existingProduct.get();
            int newQuantity = product.getStockQuantity() + request.getStockQuantity();
            product.setStockQuantity(newQuantity);
            
            Product savedProduct = productRepository.save(product);
            logger.info("Added {} units to existing product with SKU: {}. New total: {}", 
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
            logger.info("Product created successfully with ID: {} and SKU: {}", 
                savedProduct.getId(), savedProduct.getStockKeepingUnit());
            
            return convertToResponse(savedProduct);
        }
    }

    /**
     * Update product
     */
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        logger.info("Updating product with ID: {}", id);
        
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + id));
        
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStockKeepingUnit(request.getStockKeepingUnit());
        product.setStockQuantity(request.getStockQuantity());
        
        Product savedProduct = productRepository.save(product);
        logger.info("Product updated successfully with ID: {}", savedProduct.getId());
        
        return convertToResponse(savedProduct);
    }

    /**
     * Delete product
     */
    public void deleteProduct(Long id) {
        logger.info("Deleting product with ID: {}", id);
        
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + id);
        }
        
        productRepository.deleteById(id);
        logger.info("Product deleted successfully with ID: {}", id);
    }

    /**
     * Add stock to existing product by SKU
     */
    public ProductResponse addStock(String sku, Integer quantity) {
        logger.info("Adding {} units to product with SKU: {}", quantity, sku);
        
        Product product = productRepository.findByStockKeepingUnit(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        
        int newQuantity = product.getStockQuantity() + quantity;
        product.setStockQuantity(newQuantity);
        
        Product savedProduct = productRepository.save(product);
        logger.info("Added {} units to SKU: {}. New total: {}", quantity, sku, newQuantity);
        
        return convertToResponse(savedProduct);
    }

    /**
     * Reduce stock from existing product by SKU
     */
    public ProductResponse reduceStock(String sku, Integer quantity) {
        logger.info("Reducing {} units from product with SKU: {}", quantity, sku);
        
        Product product = productRepository.findByStockKeepingUnit(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + product.getStockQuantity() + ", Requested: " + quantity);
        }
        
        int newQuantity = product.getStockQuantity() - quantity;
        product.setStockQuantity(newQuantity);
        
        Product savedProduct = productRepository.save(product);
        logger.info("Reduced {} units from SKU: {}. New total: {}", quantity, sku, newQuantity);
        
        return convertToResponse(savedProduct);
    }

    /**
     * Get product by SKU
     */
    @Transactional(readOnly = true)
    public ProductResponse getProductBySku(String sku) {
        logger.info("Getting product by SKU: {}", sku);
        Product product = productRepository.findByStockKeepingUnit(sku)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with SKU: " + sku));
        return convertToResponse(product);
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