package com.ecom.paymentservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.dao.DataIntegrityViolationException;

import com.ecom.paymentservice.constants.PaymentServiceConstants;
import com.ecom.paymentservice.dto.GlobalErrorResponse;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handlePaymentNotFoundException(PaymentNotFoundException ex, WebRequest request) {
        logger.error(PaymentServiceConstants.LOG_PAYMENT_NOT_FOUND, ex.getMessage());
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            404,
            PaymentServiceConstants.PAYMENT_NOT_FOUND_MESSAGE + ex.getMessage(),
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<GlobalErrorResponse> handlePaymentProcessingException(PaymentProcessingException ex, WebRequest request) {
        logger.error(PaymentServiceConstants.LOG_PAYMENT_PROCESSING_FAILED, ex.getMessage());
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            500,
            PaymentServiceConstants.PAYMENT_PROCESSING_FAILED_MESSAGE,
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(InvalidPaymentStatusException.class)
    public ResponseEntity<GlobalErrorResponse> handleInvalidPaymentStatusException(InvalidPaymentStatusException ex, WebRequest request) {
        logger.error(PaymentServiceConstants.LOG_VALIDATION_ERROR, ex.getMessage());
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            PaymentServiceConstants.INVALID_PAYMENT_STATUS_MESSAGE + ex.getMessage(),
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<GlobalErrorResponse> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        logger.error("Data integrity violation: {}", ex.getMessage());
        
        String errorMessage = "Invalid data provided";
        String description = ex.getMessage();
        
        // Check if it's a foreign key constraint violation
        if (ex.getMessage() != null && ex.getMessage().contains("foreign key constraint")) {
            errorMessage = "Referenced order does not exist";
            description = "The order ID provided does not exist in the system";
        }
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            errorMessage,
            description
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error(PaymentServiceConstants.LOG_VALIDATION_ERROR, ex.getMessage());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
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
            PaymentServiceConstants.VALIDATION_ERROR_MESSAGE,
            errorMessage.toString()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error(PaymentServiceConstants.LOG_UNEXPECTED_ERROR, ex.getMessage(), ex);
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            500,
            PaymentServiceConstants.INTERNAL_SERVER_ERROR_MESSAGE,
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}