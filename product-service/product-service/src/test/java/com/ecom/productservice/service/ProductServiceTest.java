package com.ecom.productservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecom.productservice.client.OrderServiceClient;
import com.ecom.productservice.dto.OrderRequest;
import com.ecom.productservice.dto.OrderResponse;
import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.dto.ProductResponse;
import com.ecom.productservice.entity.Product;
import com.ecom.productservice.exception.ProductNotFoundException;
import com.ecom.productservice.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductService Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderServiceClient orderServiceClient;

    @InjectMocks
    private ProductService productService;

    private Product sampleProduct;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setName("Test Product");
        sampleProduct.setDescription("Test Description");
        sampleProduct.setPrice(new BigDecimal("99.99"));
        sampleProduct.setStockKeepingUnit("SKU001");
        sampleProduct.setStockQuantity(100);
        sampleProduct.setCreatedAt(LocalDateTime.now());

        productRequest = new ProductRequest();
        productRequest.setName("New Product");
        productRequest.setDescription("New Description");
        productRequest.setPrice(new BigDecimal("49.99"));
        productRequest.setStockKeepingUnit("SKU002");
        productRequest.setStockQuantity(50);
    }

    // Test: Get All Products
    @Test
    @DisplayName("Should return all products when repository has products")
    void testGetAllProducts_Success() {
        // Arrange
        List<Product> products = List.of(sampleProduct);
        when(productRepository.findAll()).thenReturn(products);

        // Act
        List<ProductResponse> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list when no products exist")
    void testGetAllProducts_EmptyList() {
        // Arrange
        when(productRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<ProductResponse> result = productService.getAllProducts();

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(productRepository, times(1)).findAll();
    }

    // Test: Get Product by ID
    @Test
    @DisplayName("Should return product when valid ID is provided")
    void testGetProductById_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        // Act
        ProductResponse result = productService.getProductById(1L);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(new BigDecimal("99.99"), result.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found")
    void testGetProductById_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(999L));
        verify(productRepository, times(1)).findById(999L);
    }

    // Test: Create Product
    @Test
    @DisplayName("Should create new product when SKU doesn't exist")
    void testCreateProduct_NewProduct() {
        // Arrange
        when(productRepository.findByStockKeepingUnit("SKU002")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product product = invocation.getArgument(0);
            product.setId(2L);
            return product;
        });

        // Act
        ProductResponse result = productService.createProduct(productRequest);

        // Assert
        assertNotNull(result);
        assertEquals("New Product", result.getName());
        verify(productRepository, times(1)).findByStockKeepingUnit("SKU002");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should add stock when product with SKU already exists")
    void testCreateProduct_ExistingProduct() {
        // Arrange
        productRequest.setStockKeepingUnit("SKU001"); // Use same SKU as sampleProduct
        productRequest.setStockQuantity(50);
        
        when(productRepository.findByStockKeepingUnit("SKU001")).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            return savedProduct;
        });

        // Act
        ProductResponse result = productService.createProduct(productRequest);

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        assertEquals(150, result.getStockQuantity()); // 100 (existing) + 50 (added) = 150
        verify(productRepository, times(1)).findByStockKeepingUnit("SKU001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    // Test: Update Product
    @Test
    @DisplayName("Should update product when valid ID and request are provided")
    void testUpdateProduct_Success() {
        // Arrange
        productRequest.setName("Updated Product");
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        // Act
        ProductResponse result = productService.updateProduct(1L, productRequest);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existent product")
    void testUpdateProduct_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> 
            productService.updateProduct(999L, productRequest));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    // Test: Delete Product
    @Test
    @DisplayName("Should delete product when valid ID is provided")
    void testDeleteProduct_Success() {
        // Arrange
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        // Act
        productService.deleteProduct(1L);

        // Assert
        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deleting non-existent product")
    void testDeleteProduct_NotFound() {
        // Arrange
        when(productRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(999L));
        verify(productRepository, times(1)).existsById(999L);
        verify(productRepository, never()).deleteById(anyLong());
    }

    // Test: Add Stock
    @Test
    @DisplayName("Should add stock to existing product")
    void testAddStock_Success() {
        // Arrange
        when(productRepository.findByStockKeepingUnit("SKU001")).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        // Act
        ProductResponse result = productService.addStock("SKU001", 50);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).findByStockKeepingUnit("SKU001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when adding stock to non-existent SKU")
    void testAddStock_NotFound() {
        // Arrange
        when(productRepository.findByStockKeepingUnit("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.addStock("INVALID", 50));
        verify(productRepository, times(1)).findByStockKeepingUnit("INVALID");
        verify(productRepository, never()).save(any(Product.class));
    }

    // Test: Reduce Stock
    @Test
    @DisplayName("Should reduce stock when sufficient quantity available")
    void testReduceStock_Success() {
        // Arrange
        when(productRepository.findByStockKeepingUnit("SKU001")).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        // Act
        ProductResponse result = productService.reduceStock("SKU001", 10);

        // Assert
        assertNotNull(result);
        verify(productRepository, times(1)).findByStockKeepingUnit("SKU001");
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when insufficient stock")
    void testReduceStock_InsufficientStock() {
        // Arrange
        when(productRepository.findByStockKeepingUnit("SKU001")).thenReturn(Optional.of(sampleProduct));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.reduceStock("SKU001", 150));
        verify(productRepository, times(1)).findByStockKeepingUnit("SKU001");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when reducing stock from non-existent SKU")
    void testReduceStock_NotFound() {
        // Arrange
        when(productRepository.findByStockKeepingUnit("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.reduceStock("INVALID", 10));
        verify(productRepository, times(1)).findByStockKeepingUnit("INVALID");
        verify(productRepository, never()).save(any(Product.class));
    }

    // Test: Get Product by SKU
    @Test
    @DisplayName("Should return product when valid SKU is provided")
    void testGetProductBySku_Success() {
        // Arrange
        when(productRepository.findByStockKeepingUnit("SKU001")).thenReturn(Optional.of(sampleProduct));

        // Act
        ProductResponse result = productService.getProductBySku("SKU001");

        // Assert
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository, times(1)).findByStockKeepingUnit("SKU001");
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when SKU not found")
    void testGetProductBySku_NotFound() {
        // Arrange
        when(productRepository.findByStockKeepingUnit("INVALID")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.getProductBySku("INVALID"));
        verify(productRepository, times(1)).findByStockKeepingUnit("INVALID");
    }

    // Test: Search Products
    @Test
    @DisplayName("Should return products matching search keyword")
    void testSearchProducts_Success() {
        // Arrange
        List<Product> products = List.of(sampleProduct);
        when(productRepository.searchProducts("Test")).thenReturn(products);

        // Act
        List<ProductResponse> result = productService.searchProducts("Test");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).searchProducts("Test");
    }

    @Test
    @DisplayName("Should return empty list when no products match search keyword")
    void testSearchProducts_NoResults() {
        // Arrange
        when(productRepository.searchProducts("NonExistent")).thenReturn(new ArrayList<>());

        // Act
        List<ProductResponse> result = productService.searchProducts("NonExistent");

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(productRepository, times(1)).searchProducts("NonExistent");
    }

    // Test: Reduce Stock by Product ID
    @Test
    @DisplayName("Should reduce stock by product ID successfully")
    void testReduceStockByProductId_Success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        // Act
        productService.reduceStockByProductId(1L, 10);

        // Assert
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product ID doesn't exist")
    void testReduceStockByProductId_NotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> productService.reduceStockByProductId(999L, 10));
        verify(productRepository, times(1)).findById(999L);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when insufficient stock by product ID")
    void testReduceStockByProductId_InsufficientStock() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.reduceStockByProductId(1L, 150));
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, never()).save(any(Product.class));
    }

    // Test: Buy Now
    @Test
    @DisplayName("Should complete buy now when product has sufficient stock")
    void testBuyNow_Success() {
        // Arrange
        OrderResponse orderResponse = new OrderResponse(1L, 1L, new BigDecimal("99.99"), "COMPLETED", "SUCCESS", LocalDateTime.now(), null);
        
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(orderServiceClient.checkout(any(OrderRequest.class), anyString())).thenReturn(orderResponse);
        when(productRepository.findByStockKeepingUnit("SKU001")).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        // Act
        OrderResponse result = productService.buyNow(1L, 1L, 2, "CREDIT_CARD", "Bearer token");

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getOrderStatus());
        verify(productRepository, times(1)).findById(1L);
        verify(orderServiceClient, times(1)).checkout(any(OrderRequest.class), anyString());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when product not found for buy now")
    void testBuyNow_ProductNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> 
            productService.buyNow(1L, 999L, 2, "CREDIT_CARD", "Bearer token"));
        verify(productRepository, times(1)).findById(999L);
        verify(orderServiceClient, never()).checkout(any(), any());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when insufficient stock for buy now")
    void testBuyNow_InsufficientStock() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            productService.buyNow(1L, 1L, 200, "CREDIT_CARD", "Bearer token"));
        verify(productRepository, times(1)).findById(1L);
        verify(orderServiceClient, never()).checkout(any(), any());
    }
}

