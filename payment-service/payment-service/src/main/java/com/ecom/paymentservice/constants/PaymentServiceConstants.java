package com.ecom.paymentservice.constants;

public class PaymentServiceConstants {
    
    // Payment Status Constants
    public static final String PAYMENT_STATUS_SUCCESS = "SUCCESS";
    public static final String PAYMENT_STATUS_FAILED = "FAILED";
    public static final String PAYMENT_STATUS_PENDING = "PENDING";
    public static final String PAYMENT_STATUS_REFUNDED = "REFUNDED";
    
    // Currency Constants
    public static final String DEFAULT_CURRENCY = "INR";
    
    // Transaction ID Prefix
    public static final String TRANSACTION_ID_PREFIX = "TXN-";
    
    // Success Rate for Payment Simulation (80%)
    public static final double PAYMENT_SUCCESS_RATE = 0.8;
    
    // Error Messages
    public static final String PAYMENT_NOT_FOUND_MESSAGE = "Payment not found with ID: ";
    public static final String INVALID_PAYMENT_STATUS_MESSAGE = "Invalid payment status: ";
    public static final String PAYMENT_PROCESSING_FAILED_MESSAGE = "Payment processing failed";
    public static final String REFUND_PROCESSING_FAILED_MESSAGE = "Refund processing failed";
    public static final String VALIDATION_ERROR_MESSAGE = "Validation failed";
    public static final String INTERNAL_SERVER_ERROR_MESSAGE = "Internal server error occurred";
    
    // Error Status Codes
    public static final String PAYMENT_NOT_FOUND_STATUS = "PAYMENT_NOT_FOUND";
    public static final String INVALID_PAYMENT_STATUS_STATUS = "INVALID_PAYMENT_STATUS";
    public static final String PAYMENT_PROCESSING_FAILED_STATUS = "PAYMENT_PROCESSING_FAILED";
    public static final String REFUND_PROCESSING_FAILED_STATUS = "REFUND_PROCESSING_FAILED";
    public static final String VALIDATION_ERROR_STATUS = "VALIDATION_ERROR";
    public static final String INTERNAL_SERVER_ERROR_STATUS = "INTERNAL_SERVER_ERROR";
    
    // Log Messages
    public static final String LOG_PROCESSING_PAYMENT = "Processing payment for order: {} by user: {}";
    public static final String LOG_PAYMENT_PROCESSED_SUCCESSFULLY = "Payment processed successfully with ID: {} and status: {}";
    public static final String LOG_GETTING_PAYMENT_BY_ID = "Getting payment by ID: {}";
    public static final String LOG_GETTING_PAYMENTS_FOR_USER = "Getting payments for user: {}";
    public static final String LOG_PAYMENT_NOT_FOUND = "Payment not found with ID: {}";
    public static final String LOG_PAYMENT_PROCESSING_FAILED = "Payment processing failed for order: {}";
    public static final String LOG_REFUND_PROCESSING_FAILED = "Refund processing failed for payment: {}";
    public static final String LOG_POST_PROCESS_PAYMENT_REQUEST = "POST /payments/process - Order: {}, User: {}, Amount: {}";
    public static final String LOG_GET_PAYMENT_BY_ID_REQUEST = "GET /payments/{} - Payment ID: {}";
    public static final String LOG_GET_USER_PAYMENTS_REQUEST = "GET /payments/user/{} - User ID: {}";
    public static final String LOG_PAYMENT_PROCESSED_SUCCESSFULLY_RESPONSE = "Payment processed successfully with ID: {}";
    public static final String LOG_PAYMENT_RETRIEVED_SUCCESSFULLY = "Payment retrieved successfully with ID: {}";
    public static final String LOG_USER_PAYMENTS_RETRIEVED_SUCCESSFULLY = "User payments retrieved successfully for user: {}";
    public static final String LOG_PAYMENT_PROCESSING_FAILED_ERROR = "Payment processing failed for order: {}, error: {}";
    public static final String LOG_PAYMENT_RETRIEVAL_FAILED_ERROR = "Payment retrieval failed for ID: {}, error: {}";
    public static final String LOG_USER_PAYMENTS_RETRIEVAL_FAILED_ERROR = "User payments retrieval failed for user: {}, error: {}";
    public static final String LOG_VALIDATION_ERROR = "Validation error: {}";
    public static final String LOG_UNEXPECTED_ERROR = "Unexpected error occurred: {}";
    
    
    public static final String BEARER_PREFIX = "Bearer ";
    public static final int BEARER_TOKEN_START_INDEX = 7;
    
    // Private constructor to prevent instantiation
    private PaymentServiceConstants() {}
}
