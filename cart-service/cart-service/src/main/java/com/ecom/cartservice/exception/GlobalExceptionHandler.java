package com.ecom.cartservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.ecom.cartservice.constants.CartServiceConstants;
import com.ecom.cartservice.dto.GlobalErrorResponse;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleCartNotFoundException(CartNotFoundException ex, WebRequest request) {
        logger.warn(CartServiceConstants.LOG_CART_NOT_FOUND, ex.getMessage());
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            404,
            CartServiceConstants.CART_NOT_FOUND_TITLE,
            ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleCartItemNotFoundException(CartItemNotFoundException ex, WebRequest request) {
        logger.warn(CartServiceConstants.LOG_CART_ITEM_NOT_FOUND, ex.getMessage());
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            404,
            CartServiceConstants.CART_ITEM_NOT_FOUND_TITLE,
            ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProductNotAvailableException.class)
    public ResponseEntity<GlobalErrorResponse> handleProductNotAvailableException(ProductNotAvailableException ex, WebRequest request) {
        logger.warn(CartServiceConstants.LOG_PRODUCT_NOT_AVAILABLE, ex.getMessage());
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            CartServiceConstants.PRODUCT_NOT_AVAILABLE_TITLE,
            ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleProductNotAvailableException(ProductNotFoundException ex, WebRequest request) {
        logger.warn(CartServiceConstants.LOG_PRODUCT_NOT_AVAILABLE, ex.getMessage());
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            CartServiceConstants.PRODUCT_NOT_AVAILABLE_TITLE,
            ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        logger.warn(CartServiceConstants.LOG_VALIDATION_ERROR, ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // Convert validation errors to a single message
        StringBuilder errorMessage = new StringBuilder();
        errors.forEach((field, message) -> {
            if (!errorMessage.isEmpty()) {
                errorMessage.append(", ");
            }
            errorMessage.append(field).append(": ").append(message);
        });

        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            CartServiceConstants.VALIDATION_FAILED_TITLE,
            errorMessage.toString()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error(CartServiceConstants.LOG_UNEXPECTED_ERROR_OCCURRED, ex);
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            500,
            CartServiceConstants.INTERNAL_SERVER_ERROR_TITLE,
            CartServiceConstants.UNEXPECTED_ERROR_MESSAGE
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}