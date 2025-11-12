package com.ecom.cartservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.ecom.cartservice.dto.ProductDto;

@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductServiceClient {

	@GetMapping("/products/{id}")
	ProductDto.ProductResponse getProductById(@PathVariable Long id,
			@RequestHeader(value = "Authorization", required = false) String authHeader);
}
