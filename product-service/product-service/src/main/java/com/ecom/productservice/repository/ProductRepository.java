package com.ecom.productservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecom.productservice.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all active products
     */
    List<Product> findByIsActiveTrue();

    /**
     * Find product by ID and active status
     */
    Optional<Product> findByIdAndIsActiveTrue(Long id);

    /**
     * Find product by SKU
     */
    Optional<Product> findBySku(String sku);

    /**
     * Check if SKU exists
     */
    boolean existsBySku(String sku);

    /**
     * Find products by category ID and active status
     */
    List<Product> findByCategoryIdAndIsActiveTrue(Long categoryId);

    /**
     * Find products by price range and active status
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByPriceRangeAndActive(@Param("minPrice") java.math.BigDecimal minPrice, 
                                           @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * Find products by category and price range
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND p.category.id = :categoryId AND p.price BETWEEN :minPrice AND :maxPrice")
    List<Product> findByCategoryAndPriceRange(@Param("categoryId") Long categoryId,
                                            @Param("minPrice") java.math.BigDecimal minPrice,
                                            @Param("maxPrice") java.math.BigDecimal maxPrice);

    /**
     * Search products by name containing text (case insensitive)
     */
    @Query("SELECT p FROM Product p WHERE p.isActive = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :searchText, '%'))")
    List<Product> findByNameContainingIgnoreCase(@Param("searchText") String searchText);
}
