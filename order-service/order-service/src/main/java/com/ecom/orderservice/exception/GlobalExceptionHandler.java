package com.ecom.orderservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ecom.orderservice.constants.OrderServiceConstants;
import com.ecom.orderservice.dto.GlobalErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleOrderNotFoundException(OrderNotFoundException ex, WebRequest request) {
        logger.error(OrderServiceConstants.LOG_ORDER_NOT_FOUND, ex.getMessage());

        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            404,
            ex.getMessage(),
            ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        logger.error(OrderServiceConstants.LOG_VALIDATION_ERROR, ex.getMessage());
        
        StringBuilder errorMessage = new StringBuilder(OrderServiceConstants.VALIDATION_FAILED_MESSAGE + ": ");
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((org.springframework.validation.FieldError) error).getField();
            String errorMsg = error.getDefaultMessage();
            errorMessage.append(fieldName).append(" - ").append(errorMsg).append("; ");
        });

        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            OrderServiceConstants.VALIDATION_FAILED_MESSAGE,
            errorMessage.toString()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<GlobalErrorResponse> handleMissingHeaderException(MissingRequestHeaderException ex) {
        logger.warn("Missing required request header: {}", ex.getHeaderName());

        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            401,
            "Authorization header is required",
            "Authorization header is required"
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error(OrderServiceConstants.LOG_UNEXPECTED_ERROR_OCCURRED, ex.getMessage(), ex);

        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            500,
            OrderServiceConstants.UNEXPECTED_ERROR_MESSAGE,
            ex.getMessage()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}