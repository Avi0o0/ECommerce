package com.ecom.paymentservice.controller;

import com.ecom.paymentservice.dto.PaymentRequest;
import com.ecom.paymentservice.dto.PaymentResponse;
import com.ecom.paymentservice.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private PaymentRequest validPaymentRequest;
    private PaymentResponse mockPaymentResponse;

    @BeforeEach
    void setUp() {
        // Set up test data
        validPaymentRequest = new PaymentRequest(1L, 1L, new BigDecimal("100.00"), "CREDIT_CARD");

        mockPaymentResponse = new PaymentResponse();
        mockPaymentResponse.setId(1L);
        mockPaymentResponse.setOrderId(1L);
        mockPaymentResponse.setUserId(1L);
        mockPaymentResponse.setAmount(new BigDecimal("100.00"));
        mockPaymentResponse.setPaymentMethod("CREDIT_CARD");
        mockPaymentResponse.setPaymentStatus("SUCCESS");
        mockPaymentResponse.setTransactionId("TX123");
        mockPaymentResponse.setCurrency("USD");
        mockPaymentResponse.setCreatedAt(LocalDateTime.now());
        mockPaymentResponse.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void processPayment_ValidRequest_ReturnsCreated() throws Exception {
        when(paymentService.processPayment(any(PaymentRequest.class))).thenReturn(mockPaymentResponse);

        mockMvc.perform(post("/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPaymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
    }

    @Test
    void processPayment_InvalidRequest_ReturnsBadRequest() throws Exception {
        PaymentRequest invalidRequest = new PaymentRequest();
        invalidRequest.setOrderId(null); // Violates @NotNull constraint

        mockMvc.perform(post("/payments/process")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPaymentById_ExistingPayment_ReturnsPayment() throws Exception {
        when(paymentService.getPaymentById(1L)).thenReturn(mockPaymentResponse);

        mockMvc.perform(get("/payments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.paymentMethod").value("CREDIT_CARD"))
                .andExpect(jsonPath("$.paymentStatus").value("SUCCESS"));
    }

    @Test
    void getUserPayments_ValidUserId_ReturnsPaymentsList() throws Exception {
        List<PaymentResponse> mockPayments = Arrays.asList(mockPaymentResponse);
        when(paymentService.getPaymentsByUserId(1L)).thenReturn(mockPayments);

        mockMvc.perform(get("/payments/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].orderId").value(1L))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].amount").value(100.00))
                .andExpect(jsonPath("$[0].paymentMethod").value("CREDIT_CARD"))
                .andExpect(jsonPath("$[0].paymentStatus").value("SUCCESS"));
    }
}