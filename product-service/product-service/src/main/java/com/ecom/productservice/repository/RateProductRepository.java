package com.ecom.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.productservice.entity.RateProduct;

@Repository
public interface RateProductRepository extends JpaRepository<RateProduct, Integer> {

}
