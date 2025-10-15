package com.ecom.userservice.constants;

/**
 * Constants class for User Service
 * Contains all hardcoded strings, messages, and values used throughout the service
 */
public final class UserServiceConstants {

    // Private constructor to prevent instantiation
    private UserServiceConstants() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // ==================== SERVICE MESSAGES ====================
    
    // User Service Messages
    public static final String USER_NOT_FOUND_MESSAGE = "User not found with ID: ";
    public static final String USER_NOT_FOUND_BY_USERNAME_MESSAGE = "User not found";
    public static final String INVALID_CREDENTIALS_MESSAGE = "Invalid username or password";
    public static final String CURRENT_PASSWORD_INCORRECT_MESSAGE = "Current password is incorrect";
    public static final String PASSWORD_INCORRECT_MESSAGE = "Password is incorrect";
    public static final String PASSWORD_IS_VALID_MESSAGE = "Password is valid";
    public static final String PASSWORD_VERIFICATION_FAILED_MESSAGE = "Password verification failed";
    public static final String PASSWORD_VERIFICATION_SYSTEM_ERROR_MESSAGE = "Password verification failed due to system error";
    
    // Success Messages
    public static final String USERS_RETRIEVED_SUCCESS_MESSAGE = "Users retrieved successfully";
    public static final String USER_RETRIEVED_SUCCESS_MESSAGE = "User retrieved successfully";
    public static final String USER_DELETED_SUCCESS_MESSAGE = "User deleted successfully";
    public static final String PASSWORD_UPDATED_SUCCESS_MESSAGE = "Password updated successfully";
    
    // ==================== ERROR RESPONSES ====================
    
    // Error Titles
    public static final String USER_NOT_FOUND_TITLE = "User Not Found";
    public static final String INVALID_CREDENTIALS_TITLE = "Invalid Credentials";
    public static final String USERNAME_ALREADY_EXISTS_TITLE = "Username Already Exists";
    public static final String INVALID_PASSWORD_TITLE = "Invalid Password";
    public static final String PASSWORD_VERIFICATION_FAILED_TITLE = "Password Verification Failed";
    public static final String AUTHENTICATION_FAILED_TITLE = "Authentication Failed";
    public static final String VALIDATION_FAILED_TITLE = "Validation Failed";
    public static final String INVALID_REQUEST_TITLE = "Invalid Request";
    public static final String METHOD_NOT_ALLOWED_TITLE = "Method Not Allowed";
    public static final String ACCESS_DENIED_TITLE = "Access Denied";
    public static final String INTERNAL_SERVER_ERROR_TITLE = "Internal Server Error";
    
    // Error Messages
    public static final String VALIDATION_FAILED_MESSAGE = "Request validation failed";
    public static final String UNEXPECTED_ERROR_MESSAGE = "An unexpected error occurred";
    public static final String ADMIN_ROLE_REQUIRED_MESSAGE = "You don't have permission to perform this action. Admin role required.";
    public static final String METHOD_NOT_SUPPORTED_MESSAGE = "Request method '%s' is not supported. Supported methods: %s";
    
    // ==================== LOG MESSAGES ====================
    
    // User Service Log Messages
    public static final String LOG_REQUEST_TO_LIST_ALL_USERS = "Request to list all users received";
    public static final String LOG_SUCCESSFULLY_RETRIEVED_USERS = "Successfully retrieved {} users from database";
    public static final String LOG_REQUEST_TO_GET_USER_BY_ID = "Request to get user by ID: {}";
    public static final String LOG_REQUEST_TO_DELETE_USER = "Request to delete user with ID: {}";
    public static final String LOG_REQUEST_TO_UPDATE_PASSWORD = "Request to update password for user ID: {}";
    public static final String LOG_REQUEST_TO_VERIFY_PASSWORD = "Request to verify password for user ID: {}";
    
    // User Operations Log Messages
    public static final String LOG_USER_NOT_FOUND_WITH_ID = "User not found with ID: {}";
    public static final String LOG_SUCCESSFULLY_RETRIEVED_USER = "Successfully retrieved user: {} with ID: {}";
    public static final String LOG_USER_DELETED_SUCCESSFULLY = "User {} deleted successfully with ID: {}";
    public static final String LOG_INVALID_CURRENT_PASSWORD = "Invalid current password for user ID: {}";
    public static final String LOG_PASSWORD_UPDATED_SUCCESSFULLY = "Password updated successfully for user: {} with ID: {}";
    public static final String LOG_PASSWORD_VERIFICATION_SUCCESSFUL = "Password verification successful for user: {} with ID: {}";
    public static final String LOG_PASSWORD_VERIFICATION_FAILED = "Password verification failed for user ID: {}";
    public static final String LOG_PASSWORD_VERIFICATION_ERROR = "Error during password verification for user ID: {} - {}";
    
    // Exception Handler Log Messages
    public static final String LOG_USER_NOT_FOUND = "User not found: {} at path: {}";
    public static final String LOG_INVALID_CREDENTIALS = "Invalid credentials: {} at path: {}";
    public static final String LOG_BAD_CREDENTIALS = "Bad credentials: {} at path: {}";
    public static final String LOG_USERNAME_ALREADY_EXISTS = "Username already exists: {} at path: {}";
    public static final String LOG_INVALID_PASSWORD = "Invalid password: {} at path: {}";
    public static final String LOG_AUTHENTICATION_ERROR = "Authentication error: {} at path: {}";
    public static final String LOG_VALIDATION_ERROR = "Validation error: {}";
    public static final String LOG_ILLEGAL_ARGUMENT = "Illegal argument: {} at path: {}";
    public static final String LOG_METHOD_NOT_SUPPORTED = "Method not supported: {} at path: {}";
    public static final String LOG_ACCESS_DENIED = "Access denied: {} at path: {}";
    public static final String LOG_UNEXPECTED_ERROR_OCCURRED = "Unexpected error occurred: {} at path: {}";
    
    // ==================== STRING VALUES ====================
    
    // Authentication
    public static final String ROLE_PREFIX = "ROLE_";
    public static final String EMPTY_STRING = "";
    public static final String UNKNOWN_METHODS = "Unknown";
    
    // ==================== NUMERIC VALUES ====================
    
    // HTTP Status Codes
    public static final int HTTP_OK = 200;
    public static final int HTTP_CREATED = 201;
    public static final int HTTP_BAD_REQUEST = 400;
    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int HTTP_FORBIDDEN = 403;
    public static final int HTTP_NOT_FOUND = 404;
    public static final int HTTP_METHOD_NOT_ALLOWED = 405;
    public static final int HTTP_INTERNAL_SERVER_ERROR = 500;
    
    // ==================== VALIDATION MESSAGES ====================
    
    // User Request Validation Messages
    public static final String USERNAME_REQUIRED_MESSAGE = "Username is required";
    public static final String USERNAME_SIZE_MESSAGE = "Username must be between 3 and 50 characters";
    public static final String PASSWORD_REQUIRED_MESSAGE = "Password is required";
    public static final String PASSWORD_SIZE_MESSAGE = "Password must be between 6 and 100 characters";
    public static final String EMAIL_REQUIRED_MESSAGE = "Email is required";
    public static final String EMAIL_FORMAT_MESSAGE = "Email format is invalid";
    public static final String CURRENT_PASSWORD_REQUIRED_MESSAGE = "Current password is required";
    public static final String NEW_PASSWORD_REQUIRED_MESSAGE = "New password is required";
    public static final String NEW_PASSWORD_SIZE_MESSAGE = "New password must be between 6 and 100 characters";
    public static final String VERIFY_PASSWORD_REQUIRED_MESSAGE = "Password is required for verification";
}
