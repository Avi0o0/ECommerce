package com.ecom.cartservice.constants;

/**
 * Constants class for Cart Service
 * Contains all hardcoded strings, messages, and values used throughout the service
 */
public final class CartServiceConstants {

    // Private constructor to prevent instantiation
    private CartServiceConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== SERVICE MESSAGES ====================
    
    // Cart Service Messages
    public static final String CART_NOT_FOUND_MESSAGE = "Cart not found for user: ";
    public static final String CART_EMPTY_MESSAGE = "Cart is empty for user: ";
    public static final String CART_ITEM_NOT_FOUND_MESSAGE = "Product %d not found in cart for user: %d";
    
    // Product Service Messages
    public static final String PRODUCT_NOT_AVAILABLE_MESSAGE = "Product not available or insufficient stock. Available: %d, Requested: %d";
    public static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found or inactive";
    public static final String PRODUCT_INSUFFICIENT_STOCK_MESSAGE = "Insufficient stock. Available: %d, Requested total: %d";
    public static final String PRODUCT_DETAILS_UNAVAILABLE_MESSAGE = "Product details are temporarily unavailable";
    public static final String PRODUCT_DETAILS_FETCH_ERROR_MESSAGE = "Could not fetch product details for product ID: %d";
    
    // Authentication Messages
    public static final String AUTHORIZATION_HEADER_REQUIRED_MESSAGE = "Authorization header required";
    public static final String ACCESS_DENIED_MESSAGE = "Access denied";
    public static final String USER_ROLE_REQUIRED_MESSAGE = "USER role required";
    public static final String CAN_ONLY_ADD_TO_OWN_CART_MESSAGE = "Can only add to your own cart";
    public static final String CAN_ONLY_REMOVE_FROM_OWN_CART_MESSAGE = "Can only remove from your own cart";
    public static final String CAN_ONLY_CLEAR_OWN_CART_MESSAGE = "Can only clear your own cart";
    public static final String CAN_ONLY_CHECKOUT_OWN_CART_MESSAGE = "Can only checkout your own cart";
    
    // ==================== ERROR RESPONSES ====================
    
    // Error Titles
    public static final String CART_NOT_FOUND_TITLE = "Cart Not Found";
    public static final String CART_ITEM_NOT_FOUND_TITLE = "Cart Item Not Found";
    public static final String PRODUCT_NOT_AVAILABLE_TITLE = "Product Not Available";
    public static final String VALIDATION_FAILED_TITLE = "Validation Failed";
    public static final String INTERNAL_SERVER_ERROR_TITLE = "Internal Server Error";
    
    // Error Messages
    public static final String VALIDATION_FAILED_MESSAGE = "Request validation failed";
    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";
    
    // ==================== LOG MESSAGES ====================
    
    // Cart Service Log Messages
    public static final String LOG_GETTING_CART_FOR_USER = "Getting cart for user: {}";
    public static final String LOG_ADDING_PRODUCT_TO_CART = "Adding product {} to cart for user: {}";
    public static final String LOG_REMOVING_PRODUCT_FROM_CART = "Removing product {} from cart for user: {}";
    public static final String LOG_CLEARING_CART_FOR_USER = "Clearing cart for user: {}";
    public static final String LOG_PROCESSING_CHECKOUT_FOR_USER = "Processing checkout for user: {}";
    public static final String LOG_CREATING_NEW_CART_FOR_USER = "Creating new cart for user: {}";
    public static final String LOG_UPDATED_QUANTITY_FOR_PRODUCT = "Updated quantity for product {} in cart {} to {}";
    public static final String LOG_ADDED_NEW_PRODUCT_TO_CART = "Added new product {} to cart {}";
    public static final String LOG_REMOVED_PRODUCT_FROM_CART = "Removed product {} from cart {}";
    public static final String LOG_CART_CLEARED_FOR_USER = "Cart cleared for user: {}";
    public static final String LOG_CHECKOUT_COMPLETED_FOR_USER = "Checkout completed for user: {}. Order ID: {}";
    
    // Controller Log Messages
    public static final String LOG_GET_CART_REQUEST = "GET /cart - Getting cart for user: {}";
    public static final String LOG_POST_ADD_TO_CART_REQUEST = "POST /cart/add - Adding product {} to cart for user: {}";
    public static final String LOG_DELETE_REMOVE_FROM_CART_REQUEST = "DELETE /cart/remove/{} - Removing product from cart for user: {}";
    public static final String LOG_DELETE_CLEAR_CART_REQUEST = "DELETE /cart/clear - Clearing cart for user: {}";
    public static final String LOG_POST_CHECKOUT_REQUEST = "POST /cart/checkout - Processing checkout for user: {}";
    public static final String LOG_NO_AUTHORIZATION_HEADER = "No authorization header provided";
    public static final String LOG_ACCESS_DENIED_USER_CANNOT_ACCESS_CART = "Access denied - user {} cannot access cart for user {}";
    public static final String LOG_ACCESS_DENIED_USER_NO_USER_ROLE = "Access denied - user does not have USER role";
    public static final String LOG_ACCESS_DENIED_USER_CANNOT_ADD_TO_CART = "Access denied - user {} cannot add to cart for user {}";
    public static final String LOG_ACCESS_DENIED_USER_CANNOT_REMOVE_FROM_CART = "Access denied - user {} cannot remove from cart for user {}";
    public static final String LOG_ACCESS_DENIED_USER_CANNOT_CLEAR_CART = "Access denied - user {} cannot clear cart for user {}";
    public static final String LOG_ACCESS_DENIED_USER_CANNOT_CHECKOUT_CART = "Access denied - user {} cannot checkout cart for user {}";
    
    // Authentication Service Log Messages
    public static final String LOG_VALIDATING_TOKEN_FOR_ADMIN_CHECK = "Validating token with User Service for admin check";
    public static final String LOG_VALIDATING_TOKEN_WITH_USER_SERVICE = "Validating token with User Service";
    public static final String LOG_VALIDATING_TOKEN_AND_GETTING_DETAILS = "Validating token with User Service and getting details";
    public static final String LOG_TOKEN_VALIDATION_RESPONSE = "Token validation response: valid={}, username={}, roles={}";
    public static final String LOG_TOKEN_VALIDATION_RESPONSE_SIMPLE = "Token validation response: valid={}, username={}";
    public static final String LOG_ERROR_VALIDATING_TOKEN_FOR_ADMIN_CHECK = "Error validating token for admin check: {}";
    public static final String LOG_ERROR_VALIDATING_TOKEN = "Error validating token: {}";
    public static final String LOG_ERROR_VALIDATING_TOKEN_AND_GETTING_DETAILS = "Error validating token and getting details: {}";
    public static final String LOG_ERROR_CHECKING_IF_USER = "Error checking if user: {}";
    public static final String LOG_ERROR_GETTING_USERNAME = "Error getting username: {}";
    
    // Exception Handler Log Messages
    public static final String LOG_CART_NOT_FOUND = "Cart not found: {}";
    public static final String LOG_CART_ITEM_NOT_FOUND = "Cart item not found: {}";
    public static final String LOG_PRODUCT_NOT_AVAILABLE = "Product not available: {}";
    public static final String LOG_VALIDATION_ERROR = "Validation error: {}";
    public static final String LOG_UNEXPECTED_ERROR_OCCURRED = "Unexpected error occurred";
    
    // ==================== STRING VALUES ====================
    
    // Authentication
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
    
    // Product Details Fallback
    public static final String PRODUCT_NAME_PREFIX = "Product ";
    public static final String EMPTY_STRING = "";
    
    // Order Status
    public static final String ORDER_STATUS_PENDING = "PENDING";
    
    // ==================== NUMERIC VALUES ====================
    
    // Bearer token substring start index
    public static final int BEARER_TOKEN_START_INDEX = 7;
    
    // Default values
    public static final int DEFAULT_QUANTITY = 1;
}
