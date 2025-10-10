package com.ecom.productservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.productservice.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find all active categories
     */
    List<Category> findByIsActiveTrue();

    /**
     * Find category by ID and active status
     */
    Optional<Category> findByIdAndIsActiveTrue(Long id);

    /**
     * Find category by name
     */
    Optional<Category> findByName(String name);

    /**
     * Check if category name exists
     */
    boolean existsByName(String name);
}
