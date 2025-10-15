package com.ecom.productservice.constants;

/**
 * Constants class for Product Service
 * Contains all hardcoded strings, messages, and values used throughout the service
 */
public final class ProductServiceConstants {

    // Private constructor to prevent instantiation
    private ProductServiceConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== SERVICE MESSAGES ====================
    
    // Product Service Messages
    public static final String PRODUCT_NOT_FOUND_BY_ID_MESSAGE = "Product not found with ID: ";
    public static final String PRODUCT_NOT_FOUND_BY_SKU_MESSAGE = "Product not found with SKU: ";
    public static final String INSUFFICIENT_STOCK_MESSAGE = "Insufficient stock. Available: %d, Requested: %d";
    
    // Authentication Messages
    public static final String AUTHORIZATION_HEADER_REQUIRED_MESSAGE = "Authorization header required";
    public static final String ADMIN_ACCESS_REQUIRED_MESSAGE = "Admin access required";
    
    // Success Messages
    public static final String PRODUCT_DELETED_SUCCESS_MESSAGE = "Product deleted successfully";
    
    // ==================== ERROR RESPONSES ====================
    
    // Error Titles
    public static final String PRODUCT_NOT_FOUND_TITLE = "Product Not Found";
    public static final String VALIDATION_FAILED_TITLE = "Validation Failed";
    public static final String INTERNAL_SERVER_ERROR_TITLE = "Internal Server Error";
    
    // Error Messages
    public static final String VALIDATION_FAILED_MESSAGE = "Request validation failed";
    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";
    
    // ==================== LOG MESSAGES ====================
    
    // Product Service Log Messages
    public static final String LOG_GETTING_ALL_PRODUCTS = "Getting all products";
    public static final String LOG_GETTING_PRODUCT_BY_ID = "Getting product by ID: {}";
    public static final String LOG_CREATING_NEW_PRODUCT = "Creating new product with name: {} and SKU: {}";
    public static final String LOG_UPDATING_PRODUCT = "Updating product with ID: {}";
    public static final String LOG_DELETING_PRODUCT = "Deleting product with ID: {}";
    public static final String LOG_ADDING_STOCK_TO_PRODUCT = "Adding {} units to product with SKU: {}";
    public static final String LOG_REDUCING_STOCK_FROM_PRODUCT = "Reducing {} units from product with SKU: {}";
    public static final String LOG_GETTING_PRODUCT_BY_SKU = "Getting product by SKU: {}";
    
    // Product Creation/Update Log Messages
    public static final String LOG_ADDED_STOCK_TO_EXISTING_PRODUCT = "Added {} units to existing product with SKU: {}. New total: {}";
    public static final String LOG_PRODUCT_CREATED_SUCCESSFULLY = "Product created successfully with ID: {} and SKU: {}";
    public static final String LOG_PRODUCT_UPDATED_SUCCESSFULLY = "Product updated successfully with ID: {}";
    public static final String LOG_PRODUCT_DELETED_SUCCESSFULLY = "Product deleted successfully with ID: {}";
    public static final String LOG_STOCK_ADDED_SUCCESSFULLY = "Added {} units to SKU: {}. New total: {}";
    public static final String LOG_STOCK_REDUCED_SUCCESSFULLY = "Reduced {} units from SKU: {}. New total: {}";
    
    // Controller Log Messages
    public static final String LOG_GET_ALL_PRODUCTS_REQUEST = "GET /products - Getting all products";
    public static final String LOG_GET_PRODUCT_BY_ID_REQUEST = "GET /products/{} - Getting product by ID";
    public static final String LOG_POST_CREATE_PRODUCT_REQUEST = "POST /products - Creating new product: {}";
    public static final String LOG_PUT_UPDATE_PRODUCT_REQUEST = "PUT /products/{} - Updating product";
    public static final String LOG_DELETE_PRODUCT_REQUEST = "DELETE /products/{} - Deleting product";
    public static final String LOG_GET_PRODUCT_BY_SKU_REQUEST = "GET /products/sku/{} - Getting product by SKU";
    public static final String LOG_PUT_ADD_STOCK_REQUEST = "PUT /products/{}/stock/add - Adding {} units";
    public static final String LOG_PUT_REDUCE_STOCK_REQUEST = "PUT /products/{}/stock/reduce - Reducing {} units";
    
    // Authentication Log Messages
    public static final String LOG_NO_AUTHORIZATION_HEADER = "No authorization header provided";
    public static final String LOG_ACCESS_DENIED_NOT_ADMIN = "Access denied - user is not admin";
    public static final String LOG_VALIDATING_TOKEN_WITH_USER_SERVICE = "Validating token with User Service";
    public static final String LOG_TOKEN_VALIDATION_RESPONSE = "Token validation response: valid={}, username={}, roles={}";
    public static final String LOG_ERROR_VALIDATING_TOKEN = "Error validating token: {}";
    
    // Exception Handler Log Messages
    public static final String LOG_PRODUCT_NOT_FOUND = "Product not found: {}";
    public static final String LOG_VALIDATION_ERROR = "Validation error: {}";
    public static final String LOG_UNEXPECTED_ERROR_OCCURRED = "Unexpected error occurred";
    
    // ==================== STRING VALUES ====================
    
    // Authentication
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    
    // ==================== NUMERIC VALUES ====================
    
    // Bearer token substring start index
    public static final int BEARER_TOKEN_START_INDEX = 7;
    
    // HTTP Status Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    
    // ==================== VALIDATION MESSAGES ====================
    
    // Product Request Validation Messages
    public static final String PRODUCT_NAME_REQUIRED_MESSAGE = "Product name is required";
    public static final String PRODUCT_NAME_SIZE_MESSAGE = "Product name must not exceed 200 characters";
    public static final String PRODUCT_DESCRIPTION_SIZE_MESSAGE = "Product description must not exceed 1000 characters";
    public static final String PRODUCT_PRICE_POSITIVE_MESSAGE = "Product price must be positive";
    public static final String PRODUCT_SKU_REQUIRED_MESSAGE = "Product SKU is required";
    public static final String PRODUCT_SKU_SIZE_MESSAGE = "Product SKU must not exceed 50 characters";
    public static final String PRODUCT_STOCK_QUANTITY_NON_NEGATIVE_MESSAGE = "Stock quantity must be non-negative";
}
