package com.ecom.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecom.productservice.entity.RateProduct;

public interface RateProductRepository extends JpaRepository<RateProduct, Integer> {

	
}
