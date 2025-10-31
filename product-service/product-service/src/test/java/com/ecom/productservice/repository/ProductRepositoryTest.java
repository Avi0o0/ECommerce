package com.ecom.productservice.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;


import com.ecom.productservice.entity.Product;

@DataJpaTest
@AutoConfigureTestDatabase
@DisplayName("Product Repository Tests")
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should save product successfully")
    void shouldSaveProduct() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockKeepingUnit("TEST-123");
        product.setStockQuantity(10);
        product.setCreatedAt(LocalDateTime.now());

        // When
        Product savedProduct = productRepository.save(product);
        entityManager.flush();
        entityManager.clear();

        // Then
        Product found = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertNotNull(found);
        assertEquals("Test Product", found.getName());
        assertEquals("TEST-123", found.getStockKeepingUnit());
        assertEquals(0, new BigDecimal("99.99").compareTo(found.getPrice()));
    }

    @Test
    @DisplayName("Should find product by SKU")
    void shouldFindProductBySku() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockKeepingUnit("TEST-123");
        product.setStockQuantity(10);
        product.setCreatedAt(LocalDateTime.now());
        entityManager.persist(product);
        entityManager.flush();

        // When
        Optional<Product> found = productRepository.findByStockKeepingUnit("TEST-123");

        // Then
        assertTrue(found.isPresent());
        assertEquals("Test Product", found.get().getName());
    }

    @Test
    @DisplayName("Should search products by keyword")
    void shouldSearchProductsByKeyword() {
        // Given
        Product product1 = new Product();
        product1.setName("Test Product 1");
        product1.setDescription("Test Description 1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setStockKeepingUnit("TEST-123");
        product1.setStockQuantity(10);
        product1.setCreatedAt(LocalDateTime.now());
        entityManager.persist(product1);

        Product product2 = new Product();
        product2.setName("Another Product");
        product2.setDescription("Contains test in description");
        product2.setPrice(new BigDecimal("149.99"));
        product2.setStockKeepingUnit("TEST-124");
        product2.setStockQuantity(5);
        product2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(product2);
        entityManager.flush();

        // When
        List<Product> found = productRepository.searchProducts("test");

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(p -> p.getName().equals("Test Product 1")));
        assertTrue(found.stream().anyMatch(p -> p.getName().equals("Another Product")));
    }

    @Test
    @DisplayName("Should update product")
    void shouldUpdateProduct() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockKeepingUnit("TEST-123");
        product.setStockQuantity(10);
        product.setCreatedAt(LocalDateTime.now());
        entityManager.persist(product);
        entityManager.flush();

        // When
        product.setStockQuantity(5);
        product.setPrice(new BigDecimal("89.99"));
        productRepository.save(product);
        entityManager.flush();
        entityManager.clear();

        // Then
        Product found = productRepository.findById(product.getId()).orElseThrow();
        assertEquals(5, found.getStockQuantity());
        assertEquals(0, new BigDecimal("89.99").compareTo(found.getPrice()));
    }

    @Test
    @DisplayName("Should delete product")
    void shouldDeleteProduct() {
        // Given
        Product product = new Product();
        product.setName("Test Product");
        product.setDescription("Test Description");
        product.setPrice(new BigDecimal("99.99"));
        product.setStockKeepingUnit("TEST-123");
        product.setStockQuantity(10);
        product.setCreatedAt(LocalDateTime.now());
        entityManager.persist(product);
        entityManager.flush();

        // When
        productRepository.delete(product);
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Product> found = productRepository.findById(product.getId());
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("Should find products by IDs")
    void shouldFindProductsByIds() {
        // Given
        Product product1 = new Product();
        product1.setName("Test Product 1");
        product1.setDescription("Test Description 1");
        product1.setPrice(new BigDecimal("99.99"));
        product1.setStockKeepingUnit("TEST-123");
        product1.setStockQuantity(10);
        product1.setCreatedAt(LocalDateTime.now());
        entityManager.persist(product1);

        Product product2 = new Product();
        product2.setName("Test Product 2");
        product2.setDescription("Test Description 2");
        product2.setPrice(new BigDecimal("149.99"));
        product2.setStockKeepingUnit("TEST-124");
        product2.setStockQuantity(5);
        product2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(product2);
        entityManager.flush();

        // When
        List<Product> found = productRepository.findAllById(List.of(product1.getId(), product2.getId()));

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(p -> p.getName().equals("Test Product 1")));
        assertTrue(found.stream().anyMatch(p -> p.getName().equals("Test Product 2")));
    }
}