package com.ecom.productservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.productservice.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find product by stock keeping unit
     */
    Optional<Product> findByStockKeepingUnit(String stockKeepingUnit);

    /**
     * Check if product exists by stock keeping unit
     */
    boolean existsByStockKeepingUnit(String stockKeepingUnit);
}