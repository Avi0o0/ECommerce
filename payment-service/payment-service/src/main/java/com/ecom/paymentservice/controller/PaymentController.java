package com.ecom.paymentservice.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.paymentservice.constants.PaymentServiceConstants;
import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.dto.PaymentResponse;
import com.ecom.paymentservice.service.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/payments")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping("/process")
    public ResponseEntity<Object> processPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        logger.info(PaymentServiceConstants.LOG_POST_PROCESS_PAYMENT_REQUEST, 
            paymentRequest.getOrderId(), paymentRequest.getUserId(), paymentRequest.getAmount());
        
        PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);
        logger.info(PaymentServiceConstants.LOG_PAYMENT_PROCESSED_SUCCESSFULLY_RESPONSE, paymentResponse.getId());
        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<Object> getPaymentById(@PathVariable Long paymentId) {
        logger.info(PaymentServiceConstants.LOG_GET_PAYMENT_BY_ID_REQUEST, paymentId, paymentId);
        
        PaymentResponse paymentResponse = paymentService.getPaymentById(paymentId);
        logger.info(PaymentServiceConstants.LOG_PAYMENT_RETRIEVED_SUCCESSFULLY, paymentId);
        return ResponseEntity.ok(paymentResponse);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<Object> getUserPayments(@PathVariable Long userId) {
        logger.info(PaymentServiceConstants.LOG_GET_USER_PAYMENTS_REQUEST, userId, userId);
        
        List<PaymentResponse> payments = paymentService.getPaymentsByUserId(userId);
        logger.info(PaymentServiceConstants.LOG_USER_PAYMENTS_RETRIEVED_SUCCESSFULLY, userId);
        return ResponseEntity.ok(payments);
    }
}