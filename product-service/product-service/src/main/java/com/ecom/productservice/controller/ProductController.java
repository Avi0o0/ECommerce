package com.ecom.productservice.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.productservice.constants.ProductServiceConstants;
import com.ecom.productservice.dto.GlobalErrorResponse;
import com.ecom.productservice.dto.OrderResponse;
import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.dto.ProductResponse;
import com.ecom.productservice.dto.RateRequest;
import com.ecom.productservice.dto.SuccessResponse;
import com.ecom.productservice.exception.ProductNotFoundException;
import com.ecom.productservice.service.ProductService;

import jakarta.validation.Valid;
import jwt.util.JwtTokenUtil;
import jwt.util.service.AuthService;

@RestController
@RequestMapping("/products")
public class ProductController {

	private static final Logger logger = LoggerFactory.getLogger(ProductController.class);
	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	public ResponseEntity<List<ProductResponse>> getAllProducts(
			@RequestParam(value = "search", required = false) String keyword,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		if (keyword != null && !keyword.trim().isEmpty()) {
			logger.info("Search request with keyword: {}", keyword);
			// store search history if user is authenticated
			try {
				if (authHeader != null && AuthService.isUser(authHeader)) {
					UUID userId = AuthService.getUserId(authHeader);
					productService.updateSearchHistory(userId.toString(), keyword);
				}
			} catch (Exception e) {
				logger.warn("Failed to record search history: {}", e.getMessage());
			}

			List<ProductResponse> products = productService.searchProducts(keyword);
			return ResponseEntity.ok(products);
		}
		logger.info(ProductServiceConstants.LOG_GET_ALL_PRODUCTS_REQUEST);
		List<ProductResponse> products = productService.getAllProducts();
		return ResponseEntity.ok(products);
	}

	@GetMapping("/recent/{userID}")
	public ResponseEntity<List<ProductResponse>> getRecentProductsForUser(
			@RequestParam(required = false, defaultValue = "3") Integer limit, @PathVariable String userID) {
		logger.info("GET /products/recent/{} - Getting recent products based on search history", userID);

		UUID userId = validateUserID(userID);
		List<ProductResponse> products = productService.getRecentProductsForUser(userId, 3);
		return ResponseEntity.ok(products);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
		logger.info(ProductServiceConstants.LOG_GET_PRODUCT_BY_ID_REQUEST, id);
		ProductResponse product = productService.getProductById(id);
		return ResponseEntity.ok(product);
	}

	@PostMapping("/{productId}/buy-now")
	public ResponseEntity<Object> buyNow(@PathVariable Long productId, @RequestParam String userId,
			@RequestParam Integer quantity, @RequestParam String paymentMethod,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		logger.info("POST /products/{}/buy-now - User: {}, Quantity: {}, Payment: {}", productId, userId, quantity,
				paymentMethod);
		
		UUID userID = validateUserID(userId);

		if (authHeader == null) {
			logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(),
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE,
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
		}

		// Check if user is authenticated and has USER role
		if (!AuthService.isUser(authHeader)) {
			logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_USER_NO_USER_ROLE);
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(),
							ProductServiceConstants.USER_ROLE_REQUIRED_MESSAGE,
							ProductServiceConstants.USER_ROLE_REQUIRED_MESSAGE));
		}

		// Check if user is buying for themselves
		UUID tokenUserId = AuthService.getUserId(authHeader);
		if (tokenUserId == null || !userID.equals(tokenUserId)) {
			logger.warn("User {} cannot buy for user {}", tokenUserId, userId);
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(),
							"You can only buy products for yourself", "You can only buy products for yourself"));
		}

		OrderResponse order = productService.buyNow(userID, productId, quantity, paymentMethod, authHeader);
		return ResponseEntity.status(HttpStatus.CREATED).body(order);
	}

	@PostMapping
	public ResponseEntity<Object> createProduct(@Valid @RequestBody ProductRequest request,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		logger.info(ProductServiceConstants.LOG_POST_CREATE_PRODUCT_REQUEST, request.getName());

		if (authHeader == null) {
			logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(),
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE,
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
		}

		if (!AuthService.isAdmin(authHeader)) {
			logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(),
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE,
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
		}

		ProductResponse product = productService.createProduct(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(product);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Object> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductRequest request,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		logger.info(ProductServiceConstants.LOG_PUT_UPDATE_PRODUCT_REQUEST, id);

		if (authHeader == null) {
			logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(),
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE,
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
		}

		if (!AuthService.isAdmin(authHeader)) {
			logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(),
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE,
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
		}

		ProductResponse product = productService.updateProduct(id, request);
		return ResponseEntity.ok(product);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteProduct(@PathVariable Long id,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		logger.info(ProductServiceConstants.LOG_DELETE_PRODUCT_REQUEST, id);

		if (authHeader == null) {
			logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(),
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE,
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
		}

		if (!AuthService.isAdmin(authHeader)) {
			logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(),
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE,
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
		}

		productService.deleteProduct(id);
		SuccessResponse response = new SuccessResponse(HttpStatus.OK.value(),
				ProductServiceConstants.PRODUCT_DELETED_SUCCESS_MESSAGE);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/sku/{sku}")
	public ResponseEntity<ProductResponse> getProductBySku(@PathVariable String sku) {
		logger.info(ProductServiceConstants.LOG_GET_PRODUCT_BY_SKU_REQUEST, sku);
		ProductResponse product = productService.getProductBySku(sku);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/{sku}/stock/add")
	public ResponseEntity<Object> addStock(@PathVariable String sku, @RequestParam Integer quantity,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		logger.info(ProductServiceConstants.LOG_PUT_ADD_STOCK_REQUEST, sku, quantity);

		if (authHeader == null) {
			logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(),
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE,
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
		}

		if (!AuthService.isAdmin(authHeader)) {
			logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(),
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE,
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
		}

		ProductResponse product = productService.addStock(sku, quantity);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/{sku}/stock/reduce")
	public ResponseEntity<Object> reduceStock(@PathVariable String sku, @RequestParam Integer quantity,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		logger.info(ProductServiceConstants.LOG_PUT_REDUCE_STOCK_REQUEST, sku, quantity);

		if (authHeader == null) {
			logger.warn(ProductServiceConstants.LOG_NO_AUTHORIZATION_HEADER);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body(new GlobalErrorResponse(HttpStatus.UNAUTHORIZED.value(),
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE,
							ProductServiceConstants.AUTHORIZATION_HEADER_REQUIRED_MESSAGE));
		}

		if (!AuthService.isAdmin(authHeader)) {
			logger.warn(ProductServiceConstants.LOG_ACCESS_DENIED_NOT_ADMIN);
			return ResponseEntity.status(HttpStatus.FORBIDDEN)
					.body(new GlobalErrorResponse(HttpStatus.FORBIDDEN.value(),
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE,
							ProductServiceConstants.ADMIN_ACCESS_REQUIRED_MESSAGE));
		}

		ProductResponse product = productService.reduceStock(sku, quantity);
		return ResponseEntity.ok(product);
	}

	@PutMapping("/reduce-stock")
	public ResponseEntity<Object> reduceStockByProductId(@RequestParam Long productId, @RequestParam Integer quantity) {
		logger.info("Reducing stock for product ID: {} by quantity: {}", productId, quantity);
		productService.reduceStockByProductId(productId, quantity);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/rate-product")
	public ResponseEntity<SuccessResponse> rateProduct(@RequestBody RateRequest rateRequest,
			@RequestHeader(value = "Authorization", required = false) String authHeader) {
		
		try {
			logger.info("check product validity");
			productService.getProductById(rateRequest.getProductId());
		} catch (Exception e) {
			throw new ProductNotFoundException(ProductServiceConstants.PRODUCT_NOT_FOUND_MESSAGE);
		} 

		try {
			logger.info("authentication check");
			if (authHeader != null && AuthService.isUser(authHeader)) {
				logger.info("authheader present");
				 String token = authHeader.startsWith(ProductServiceConstants.BEARER_PREFIX) ? 
						 authHeader.substring(ProductServiceConstants.BEARER_TOKEN_START_INDEX) : authHeader;
				 
				logger.info("Current token is {}, and has usernmae : {}", JwtTokenUtil.isTokenValid(token), JwtTokenUtil.extractUsername(token));
				UUID userId = AuthService.getUserId(authHeader);
				rateRequest.setUserId(userId.toString());
				SuccessResponse successResponse = productService.rateProduct(rateRequest);
				return ResponseEntity.ok(successResponse);
			}
		} catch (Exception e) {
			logger.warn("Failed to record search history: {}", e.getMessage());
		}

		SuccessResponse successResponse = productService.rateProduct(rateRequest);
		return ResponseEntity.ok(successResponse);
	}
	
	public UUID validateUserID(String id) {
		UUID userId = null;
		try {
			userId = UUID.fromString(id);
		} catch (Exception e) {
			throw new IllegalArgumentException("Invalid ID");
		}
		return userId;
	}
}