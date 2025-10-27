package com.ecom.orderservice.constants;

/**
 * Constants class for Order Service
 * Contains all hardcoded strings, messages, and values used throughout the service
 */
public final class OrderServiceConstants {

    // Private constructor to prevent instantiation
    private OrderServiceConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== SERVICE MESSAGES ====================
    
    // Order Service Messages
    public static final String ORDER_NOT_FOUND_MESSAGE = "Order not found with ID: ";
    public static final String ORDER_CREATED_SUCCESS_MESSAGE = "Order created successfully";
    public static final String ORDER_UPDATED_SUCCESS_MESSAGE = "Order updated successfully";
    public static final String CHECKOUT_SUCCESS_MESSAGE = "Checkout completed successfully";
    public static final String CHECKOUT_FAILED_MESSAGE = "Checkout process failed";
    
    // Payment Service Messages
    public static final String PAYMENT_SUCCESS_MESSAGE = "Payment processed successfully";
    public static final String PAYMENT_FAILED_MESSAGE = "Payment processing failed";
    public static final String PAYMENT_SERVICE_ERROR_MESSAGE = "Payment service error";
    
    // Authentication Messages
    public static final String AUTHORIZATION_HEADER_REQUIRED_MESSAGE = "Authorization header required";
    public static final String USER_ROLE_REQUIRED_MESSAGE = "USER role required";
    public static final String ADMIN_ROLE_REQUIRED_MESSAGE = "ADMIN role required";
    public static final String INVALID_TOKEN_MESSAGE = "Invalid or expired token";
    public static final String UNAUTHORIZED_ACCESS_MESSAGE = "Unauthorized access";
    
    // ==================== ERROR RESPONSES ====================
    
    // Error Status Codes
    public static final String ORDER_NOT_FOUND_STATUS = "ORDER_NOT_FOUND";
    public static final String VALIDATION_FAILED_STATUS = "VALIDATION_FAILED";
    public static final String CHECKOUT_FAILED_STATUS = "CHECKOUT_FAILED";
    public static final String PAYMENT_FAILED_STATUS = "PAYMENT_FAILED";
    public static final String PAYMENT_SERVICE_UNAVAILABLE_STATUS = "PAYMENT_SERVICE_UNAVAILABLE";
    public static final String UNAUTHORIZED_STATUS = "UNAUTHORIZED";
    public static final String FETCH_ERROR_STATUS = "FETCH_ERROR";
    public static final String INTERNAL_SERVER_ERROR_STATUS = "INTERNAL_SERVER_ERROR";
    
    // Error Messages
    public static final String VALIDATION_FAILED_MESSAGE = "Request validation failed";
    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";
    public static final String PAYMENT_PROCESSING_ERROR_MESSAGE = "Error processing payment";
    public static final String PAYMENT_SERVICE_UNAVAILABLE_MESSAGE = "Payment service not available, try again later";
    public static final String ORDER_CREATION_ERROR_MESSAGE = "Error creating order";
    
    // ==================== LOG MESSAGES ====================
    
    // Order Service Log Messages
    public static final String LOG_STARTING_CHECKOUT_PROCESS = "Starting checkout process for user: {}";
    public static final String LOG_ORDER_CREATED_WITH_STATUS = "Order created with ID: {} and status: PENDING";
    public static final String LOG_ORDER_ITEMS_CREATED = "Order items created for order ID: {}";
    public static final String LOG_CALLING_PAYMENT_SERVICE = "Calling payment service for order ID: {}";
    public static final String LOG_PAYMENT_SUCCESSFUL = "Payment successful for order ID: {}, payment ID: {}";
    public static final String LOG_PAYMENT_FAILED = "Payment failed for order ID: {}";
    public static final String LOG_PAYMENT_SERVICE_UNAVAILABLE = "Payment service unavailable for order ID: {}";
    public static final String LOG_ORDER_STATUS_UPDATED = "Order status updated to: {} for order ID: {}";
    public static final String LOG_CHECKOUT_COMPLETED_SUCCESSFULLY = "Checkout completed successfully for order ID: {}";
    public static final String LOG_ERROR_DURING_CHECKOUT = "Error during checkout process for user: {}, error: {}";
    public static final String LOG_ERROR_UPDATING_ORDER_STATUS = "Error updating order status to FAILED: {}";
    public static final String LOG_GETTING_ORDER_BY_ID = "Getting order by ID: {}";
    public static final String LOG_GETTING_ORDERS_FOR_USER = "Getting orders for user: {}";
    
    // Controller Log Messages
    public static final String LOG_POST_CHECKOUT_REQUEST = "POST /orders/checkout - Starting checkout for user: {}";
    public static final String LOG_GET_ORDER_BY_ID_REQUEST = "GET /orders/{} - Getting order by ID";
    public static final String LOG_GET_ORDERS_BY_USER_REQUEST = "GET /orders/user/{} - Getting orders for user";
    public static final String LOG_UNAUTHORIZED_CHECKOUT_ATTEMPT = "Unauthorized checkout attempt for user: {}";
    public static final String LOG_UNAUTHORIZED_ACCESS_ATTEMPT_ORDER = "Unauthorized access attempt for order ID: {}";
    public static final String LOG_UNAUTHORIZED_ACCESS_ATTEMPT_USER_ORDERS = "Unauthorized access attempt for user orders: {}";
    public static final String LOG_CHECKOUT_FAILED_FOR_USER = "Checkout failed for user: {}, error: {}";
    public static final String LOG_ERROR_GETTING_ORDER_BY_ID = "Error getting order by ID: {}, error: {}";
    public static final String LOG_ERROR_GETTING_ORDERS_FOR_USER = "Error getting orders for user: {}, error: {}";
    
    // Authentication Service Log Messages
    public static final String LOG_VALIDATING_TOKEN_FOR_ADMIN_CHECK = "Validating token for admin check";
    public static final String LOG_VALIDATING_TOKEN_WITH_USER_SERVICE = "Validating token with user service";
    public static final String LOG_VALIDATING_TOKEN_FOR_USER_CHECK = "Validating token for user check";
    public static final String LOG_VALIDATING_TOKEN_AND_GETTING_DETAILS = "Validating token and getting details";
    public static final String LOG_TOKEN_VALIDATION_RESPONSE = "Token validation response - Valid: {}, Username: {}, Roles: {}";
    public static final String LOG_TOKEN_VALIDATION_RESPONSE_SIMPLE = "Token validation response - Valid: {}, Username: {}";
    public static final String LOG_ERROR_VALIDATING_TOKEN_FOR_ADMIN_CHECK = "Error validating token for admin check: {}";
    public static final String LOG_ERROR_VALIDATING_TOKEN = "Error validating token: {}";
    public static final String LOG_ERROR_VALIDATING_TOKEN_FOR_USER_CHECK = "Error validating token for user check: {}";
    public static final String LOG_ERROR_VALIDATING_TOKEN_AND_GETTING_DETAILS = "Error validating token and getting details: {}";
    
    // Exception Handler Log Messages
    public static final String LOG_ORDER_NOT_FOUND = "Order not found: {}";
    public static final String LOG_VALIDATION_ERROR = "Validation error: {}";
    public static final String LOG_UNEXPECTED_ERROR_OCCURRED = "Unexpected error occurred: {}";
    
    // ==================== STRING VALUES ====================
    
    // Authentication
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    
    // Order Status Values
    public static final String ORDER_STATUS_PENDING = "PENDING";
    public static final String ORDER_STATUS_COMPLETED = "COMPLETED";
    public static final String ORDER_STATUS_FAILED = "FAILED";
    public static final String ORDER_STATUS_INCOMPLETE = "INCOMPLETE";
    
    // Payment Status Values
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_SUCCESS = "SUCCESS";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
    
    // Payment Service Response Values
    public static final String PAYMENT_RESPONSE_SUCCESS = "SUCCESS";
    
    // ==================== NUMERIC VALUES ====================
    
    // Bearer token substring start index
    public static final int BEARER_TOKEN_START_INDEX = 7;
    
    // HTTP Status Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    
    // ==================== VALIDATION MESSAGES ====================
    
    // Order Request Validation Messages
    public static final String USER_ID_REQUIRED_MESSAGE = "User ID is required";
    public static final String TOTAL_AMOUNT_REQUIRED_MESSAGE = "Total amount is required";
    public static final String TOTAL_AMOUNT_MIN_VALUE_MESSAGE = "Total amount must be greater than 0";
    public static final String PAYMENT_METHOD_REQUIRED_MESSAGE = "Payment method is required";
    public static final String ORDER_ITEMS_REQUIRED_MESSAGE = "Order items are required";
    
    // Order Item Request Validation Messages
    public static final String PRODUCT_ID_REQUIRED_MESSAGE = "Product ID is required";
    public static final String QUANTITY_MIN_VALUE_MESSAGE = "Quantity must be at least 1";
    public static final String PRICE_REQUIRED_MESSAGE = "Price is required";
    public static final String PRICE_MIN_VALUE_MESSAGE = "Price must be greater than 0";
    
    // Payment Request Validation Messages
    public static final String ORDER_ID_REQUIRED_MESSAGE = "Order ID is required";
    public static final String AMOUNT_REQUIRED_MESSAGE = "Amount is required";
    public static final String AMOUNT_MIN_VALUE_MESSAGE = "Amount must be greater than 0";
}
