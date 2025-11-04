package com.ecom.productservice.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ecom.productservice.constants.ProductServiceConstants;
import com.ecom.productservice.dto.GlobalErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleProductNotFoundException(ProductNotFoundException ex, WebRequest request) {
        logger.error(ProductServiceConstants.LOG_PRODUCT_NOT_FOUND, ex.getMessage());
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            404,
            ProductServiceConstants.PRODUCT_NOT_FOUND_MESSAGE,
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<GlobalErrorResponse> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
        logger.error("Authentication error: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            401,
            "Authentication Failed",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error(ProductServiceConstants.LOG_VALIDATION_ERROR, ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        // Convert validation errors to a single message
        StringBuilder errorMessage = new StringBuilder();
        errors.forEach((field, message) -> {
            if (errorMessage.length() > 0) {
                errorMessage.append(", ");
            }
            errorMessage.append(field).append(": ").append(message);
        });
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            ProductServiceConstants.VALIDATION_ERROR_MESSAGE,
            errorMessage.toString()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error(ProductServiceConstants.LOG_UNEXPECTED_ERROR, ex.getMessage(), ex);
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            500,
            ProductServiceConstants.INTERNAL_SERVER_ERROR_MESSAGE,
            "An unexpected error occurred"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(AvailabilityException.class)
    public ResponseEntity<GlobalErrorResponse> handleAvailabilityException(AvailabilityException ex) {
        logger.error("Availability error: {} due to: {}", ex.getMessage(), ex.getMessage());
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            "Check input quantity",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
}