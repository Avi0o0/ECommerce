package com.ecom.apigateway.exception;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.ecom.apigateway.dto.GlobalErrorResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(Exception.class)
	public ResponseEntity<GlobalErrorResponse> handleGenericException(Exception ex, WebRequest request) {
		logger.error("Unexpected error occurred: {}", ex.getMessage());

		GlobalErrorResponse errorResponse = new GlobalErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
				"Unexpected error occurred: {}", ex.getMessage());

		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler({ ConnectException.class, SocketTimeoutException.class })
	protected ResponseEntity<Object> handleServiceUnavailable(Exception ex, WebRequest request) {
		GlobalErrorResponse errorResponse = new GlobalErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(),
				"Service temporarily unavailable", ex.getLocalizedMessage());
		return new ResponseEntity<>(errorResponse, HttpStatus.SERVICE_UNAVAILABLE);
	}

}
