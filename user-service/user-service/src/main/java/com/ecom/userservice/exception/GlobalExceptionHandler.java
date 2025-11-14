package com.ecom.userservice.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ecom.userservice.constants.UserServiceConstants;
import com.ecom.userservice.dto.GlobalErrorResponse;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<GlobalErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        logger.error(UserServiceConstants.LOG_USER_NOT_FOUND, ex.getMessage(), request.getDescription(false));
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            404,
            "User Not Found",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<GlobalErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex, WebRequest request) {
        logger.error(UserServiceConstants.LOG_INVALID_CREDENTIALS, ex.getMessage(), request.getDescription(false));
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            401,
            "Invalid Credentials",
            UserServiceConstants.INVALID_CREDENTIALS_MESSAGE
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<GlobalErrorResponse> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        logger.error(UserServiceConstants.LOG_BAD_CREDENTIALS, ex.getMessage(), request.getDescription(false));
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            401,
            "Invalid Credentials",
            UserServiceConstants.INVALID_CREDENTIALS_MESSAGE
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<GlobalErrorResponse> handleUsernameAlreadyExistsException(UsernameAlreadyExistsException ex, WebRequest request) {
        logger.error("Username already exists: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            409,
            "Username Already Exists",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<GlobalErrorResponse> handleInvalidPasswordException(InvalidPasswordException ex, WebRequest request) {
        logger.error("Invalid password: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            "Invalid Password",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(PasswordVerificationFailedException.class)
    public ResponseEntity<GlobalErrorResponse> handlePasswordVerificationFailedException(PasswordVerificationFailedException ex, WebRequest request) {
        logger.error("Password verification failed: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            401,
            "Password Verification Failed",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
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
        logger.error("Validation error: {}", ex.getMessage());
        
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
            "Validation Failed",
            errorMessage.toString()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<GlobalErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        logger.error("Illegal argument: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            400,
            "Invalid Request",
            ex.getMessage()
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<GlobalErrorResponse> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        logger.error("Method not supported: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        String supportedMethods = ex.getSupportedMethods() != null ? String.join(", ", ex.getSupportedMethods()) : "Unknown";
        String message = String.format("Request method '%s' is not supported. Supported methods: %s", ex.getMethod(), supportedMethods);
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            405,
            "Method Not Allowed",
            message
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<GlobalErrorResponse> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("Access denied: {} at path: {}", ex.getMessage(), request.getDescription(false));
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            403,
            "Access Denied",
            "You don't have permission to perform this action. Admin role required."
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        logger.error("Unexpected error occurred: {} at path: {}", ex.getMessage(), request.getDescription(false), ex);
        
        GlobalErrorResponse errorResponse = new GlobalErrorResponse(
            500,
            "Internal Server Error",
            "An unexpected error occurred"
        );
        
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}