package com.ecom.paymentservice.controller;

import com.ecom.paymentservice.dto.PaymentDto;
import com.ecom.paymentservice.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/payments")
@Tag(name = "Payment Management", description = "APIs for managing payments")
public class PaymentController {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
    
    private final PaymentService paymentService;
    
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    @PostMapping("/process")
    @Operation(summary = "Process payment", description = "Process payment for an order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Payment processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentDto.PaymentResponse> processPayment(
            @Valid @RequestBody PaymentDto.PaymentRequest paymentRequest) {
        logger.info("Received request to process payment for order: {}", paymentRequest.getOrderId());
        
        PaymentDto.PaymentResponse paymentResponse = paymentService.processPayment(paymentRequest);
        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }
    
    @GetMapping("/{paymentId}")
    @Operation(summary = "Get payment by ID", description = "Retrieve payment details by payment ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentDto.PaymentResponse> getPaymentById(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId) {
        logger.info("Received request to get payment: {}", paymentId);
        
        PaymentDto.PaymentResponse paymentResponse = paymentService.getPaymentById(paymentId);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }
    
    @GetMapping("/transaction/{transactionId}")
    @Operation(summary = "Get payment by transaction ID", description = "Retrieve payment details by transaction ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentDto.PaymentResponse> getPaymentByTransactionId(
            @Parameter(description = "Transaction ID") @PathVariable String transactionId) {
        logger.info("Received request to get payment by transaction ID: {}", transactionId);
        
        PaymentDto.PaymentResponse paymentResponse = paymentService.getPaymentByTransactionId(transactionId);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }
    
    @GetMapping("/order/{orderId}/user/{userId}")
    @Operation(summary = "Get payment by order ID and user ID", description = "Retrieve payment details by order ID and user ID for security")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment found"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentDto.PaymentResponse> getPaymentByOrderIdAndUserId(
            @Parameter(description = "Order ID") @PathVariable Long orderId,
            @Parameter(description = "User ID") @PathVariable Long userId) {
        logger.info("Received request to get payment for order {} and user: {}", orderId, userId);
        
        PaymentDto.PaymentResponse paymentResponse = paymentService.getPaymentByOrderIdAndUserId(orderId, userId);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get payment history by user ID", description = "Retrieve all payments for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentDto.PaymentResponse>> getPaymentHistoryByUserId(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        logger.info("Received request to get payment history for user: {}", userId);
        
        List<PaymentDto.PaymentResponse> payments = paymentService.getPaymentHistoryByUserId(userId);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/paged")
    @Operation(summary = "Get paginated payment history by user ID", description = "Retrieve paginated payments for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Page<PaymentDto.PaymentResponse>> getPaymentHistoryByUserIdPaged(
            @Parameter(description = "User ID") @PathVariable Long userId,
            Pageable pageable) {
        logger.info("Received request to get paginated payment history for user: {}", userId);
        
        Page<PaymentDto.PaymentResponse> payments = paymentService.getPaymentHistoryByUserId(userId, pageable);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Retrieve all payments with a specific status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentDto.PaymentResponse>> getPaymentsByStatus(
            @Parameter(description = "Payment status") @PathVariable String status) {
        logger.info("Received request to get payments by status: {}", status);
        
        List<PaymentDto.PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
    
    @GetMapping("/method/{paymentMethod}")
    @Operation(summary = "Get payments by payment method", description = "Retrieve all payments with a specific payment method")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payments retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<PaymentDto.PaymentResponse>> getPaymentsByPaymentMethod(
            @Parameter(description = "Payment method") @PathVariable String paymentMethod) {
        logger.info("Received request to get payments by payment method: {}", paymentMethod);
        
        List<PaymentDto.PaymentResponse> payments = paymentService.getPaymentsByPaymentMethod(paymentMethod);
        return new ResponseEntity<>(payments, HttpStatus.OK);
    }
    
    @PutMapping("/{paymentId}/status")
    @Operation(summary = "Update payment status", description = "Update the status of an existing payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Payment status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status or status transition"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentDto.PaymentResponse> updatePaymentStatus(
            @Parameter(description = "Payment ID") @PathVariable Long paymentId,
            @Valid @RequestBody PaymentDto.PaymentStatusUpdateRequest statusUpdateRequest) {
        logger.info("Received request to update payment {} status to: {}", paymentId, statusUpdateRequest.getPaymentStatus());
        
        PaymentDto.PaymentResponse paymentResponse = paymentService.updatePaymentStatus(paymentId, statusUpdateRequest.getPaymentStatus());
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }
    
    @PostMapping("/refund")
    @Operation(summary = "Process refund", description = "Process refund for a successful payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Refund processed successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid refund request"),
        @ApiResponse(responseCode = "404", description = "Payment not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentDto.PaymentResponse> processRefund(
            @Valid @RequestBody PaymentDto.RefundRequest refundRequest) {
        logger.info("Received request to process refund for payment: {}", refundRequest.getPaymentId());
        
        PaymentDto.PaymentResponse paymentResponse = paymentService.processRefund(refundRequest);
        return new ResponseEntity<>(paymentResponse, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/total")
    @Operation(summary = "Get total amount paid by user", description = "Calculate total amount paid by a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Total amount calculated successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<BigDecimal> getTotalAmountPaidByUser(
            @Parameter(description = "User ID") @PathVariable Long userId) {
        logger.info("Received request to get total amount paid by user: {}", userId);
        
        BigDecimal totalAmount = paymentService.getTotalAmountPaidByUser(userId);
        return new ResponseEntity<>(totalAmount, HttpStatus.OK);
    }
}
