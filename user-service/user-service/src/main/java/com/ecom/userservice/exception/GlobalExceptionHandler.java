package com.ecom.userservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ecom.userservice.constants.UserServiceConstants;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        logger.error(UserServiceConstants.LOG_USER_NOT_FOUND, ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            UserServiceConstants.USER_NOT_FOUND_TITLE,
            ex.getMessage(),
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        logger.error(UserServiceConstants.LOG_INVALID_CREDENTIALS, ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            UserServiceConstants.INVALID_CREDENTIALS_TITLE,
            UserServiceConstants.INVALID_CREDENTIALS_MESSAGE,
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        logger.error(UserServiceConstants.LOG_BAD_CREDENTIALS, ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            UserServiceConstants.INVALID_CREDENTIALS_TITLE,
            UserServiceConstants.INVALID_CREDENTIALS_MESSAGE,
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex, WebRequest request) {
        logger.error("Username already exists: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Username Already Exists",
            ex.getMessage(),
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex, WebRequest request) {
        logger.error("Invalid password: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Password",
            ex.getMessage(),
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(PasswordVerificationFailedException.class)
    public ResponseEntity<ErrorResponse> handlePasswordVerificationFailedException(PasswordVerificationFailedException ex, WebRequest request) {
        logger.error("Password verification failed: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            UserServiceConstants.PASSWORD_VERIFICATION_FAILED_TITLE,
            ex.getMessage(),
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.error("Authentication error: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.UNAUTHORIZED.value(),
            "Authentication Failed",
            ex.getMessage(),
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error("Validation error: {}", ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ValidationErrorResponse errorResponse = new ValidationErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "Request validation failed",
            LocalDateTime.now(),
            errors
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("Illegal argument: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Invalid Request",
            ex.getMessage(),
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException ex, WebRequest request) {
        logger.error("Method not supported: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        String supportedMethods = ex.getSupportedMethods() != null ? String.join(", ", ex.getSupportedMethods()) : "Unknown";
        String message = String.format("Request method '%s' is not supported. Supported methods: %s", ex.getMethod(), supportedMethods);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.METHOD_NOT_ALLOWED.value(),
            "Method Not Allowed",
            message,
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex, WebRequest request) {
        logger.error("Access denied: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            "Access Denied",
            "You don't have permission to perform this action. Admin role required.",
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred: {} at path: {}", ex.getMessage(), request.getDescription(false), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred",
            LocalDateTime.now(),
            UserServiceConstants.EMPTY_STRING // Remove path from response
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}