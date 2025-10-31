package com.ecom.orderservice.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import com.ecom.orderservice.entity.OrderStatus;
import com.ecom.orderservice.dto.TokenValidationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ecom.orderservice.dto.OrderItemRequest;
import com.ecom.orderservice.dto.OrderRequest;
import com.ecom.orderservice.dto.OrderResponse;
import com.ecom.orderservice.service.AuthenticationService;
import com.ecom.orderservice.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Order Controller Tests")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private AuthenticationService authenticationService;

    private OrderRequest orderRequest;
    private OrderResponse orderResponse;
    private String authHeader;
    private OrderItemRequest orderItemRequest;

    @BeforeEach
    void setUp() {
        orderItemRequest = new OrderItemRequest();
        orderItemRequest.setProductId(1L);
        orderItemRequest.setQuantity(2);
        orderItemRequest.setPrice(new BigDecimal("99.99"));

        orderRequest = new OrderRequest();
        orderRequest.setUserId(1L);
        orderRequest.setTotalAmount(new BigDecimal("199.98"));
        orderRequest.setPaymentMethod("CREDIT_CARD");
        orderRequest.setOrderItems(Arrays.asList(orderItemRequest));

        orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setUserId(1L);
        orderResponse.setTotalAmount(new BigDecimal("199.98"));
        orderResponse.setOrderStatus(OrderStatus.COMPLETED);
        orderResponse.setPaymentStatus("SUCCESS");

        authHeader = "Bearer test.jwt.token";
    }

    @Test
    @DisplayName("Should create order when authorized")
    void shouldCreateOrder_whenAuthorized() throws Exception {
        // Given
        when(authenticationService.isUser(authHeader)).thenReturn(true);
        when(orderService.checkout(any(OrderRequest.class))).thenReturn(orderResponse);
        orderResponse.setOrderStatus(OrderStatus.COMPLETED); // Ensure status is COMPLETED

        // When/Then
        mockMvc.perform(post("/orders/checkout")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(orderResponse.getId()))
                .andExpect(jsonPath("$.userId").value(orderResponse.getUserId()))
                .andExpect(jsonPath("$.orderStatus").value("COMPLETED"))
                .andExpect(jsonPath("$.paymentStatus").value(orderResponse.getPaymentStatus()));
    }

    @Test
    @DisplayName("Should return order by ID when authorized")
    void shouldReturnOrderById_whenAuthorized() throws Exception {
        // Given
        when(authenticationService.isValidToken(authHeader)).thenReturn(true);
        when(orderService.getOrderById(1L)).thenReturn(orderResponse);

        // When/Then
        mockMvc.perform(get("/orders/1")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderResponse.getId()))
                .andExpect(jsonPath("$.userId").value(orderResponse.getUserId()));
    }

    @Test
    @DisplayName("Should return all orders by user ID when authorized")
    void shouldReturnAllOrdersByUserId_whenAuthorized() throws Exception {
        // Given
        List<OrderResponse> orders = Arrays.asList(orderResponse);
        when(authenticationService.isValidToken(authHeader)).thenReturn(true);
        when(orderService.getOrdersByUserId(1L)).thenReturn(orders);

        // When/Then
        mockMvc.perform(get("/orders/user/1")
                .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(orderResponse.getId()))
                .andExpect(jsonPath("$[0].userId").value(orderResponse.getUserId()));
    }

    @Test
    @DisplayName("Should return 401 unauthorized when no auth header")
    void shouldReturnUnauthorized_whenNoAuthHeader() throws Exception {
        // When/Then
        mockMvc.perform(post("/orders/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))  // Updated from code to status
                .andExpect(jsonPath("$.message").value("Authorization header is required"));
    }

    @Test
    @DisplayName("Should return unauthorized when token is invalid")
    void shouldReturnUnauthorized_whenInvalidToken() throws Exception {
        // Given
        when(authenticationService.isValidToken(authHeader)).thenReturn(false);

        // When/Then
        mockMvc.perform(get("/orders/user/1")
                .header("Authorization", authHeader))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return server error when order is incomplete")
    void shouldReturnServerError_whenOrderIncomplete() throws Exception {
        // Given
        when(authenticationService.isUser(authHeader)).thenReturn(true);
        orderResponse.setOrderStatus(OrderStatus.INCOMPLETE);
        when(orderService.checkout(any(OrderRequest.class))).thenReturn(orderResponse);

        // When/Then
        mockMvc.perform(post("/orders/checkout")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should return bad request when invalid order request")
    void shouldReturnBadRequest_whenInvalidOrderRequest() throws Exception {
        // Given
        orderRequest.setUserId(null); // Invalid request
        when(authenticationService.isUser(authHeader)).thenReturn(true);

        // When/Then
        mockMvc.perform(post("/orders/checkout")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isBadRequest());
    }
}