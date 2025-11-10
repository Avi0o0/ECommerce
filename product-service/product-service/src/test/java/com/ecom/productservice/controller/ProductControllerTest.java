package com.ecom.productservice.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.ecom.productservice.dto.ProductRequest;
import com.ecom.productservice.dto.ProductResponse;
import com.ecom.productservice.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jwt.util.service.AuthService;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private ProductService productService;
    
    private ProductRequest productRequest;
    private ProductResponse productResponse;
    private String authHeader;

    @BeforeEach
    void setUp() {
//        productRequest = new ProductRequest();
//        productRequest.setName("Test Product");
//        productRequest.setDescription("Test Description");
//        productRequest.setPrice(new BigDecimal("99.99"));
//        productRequest.setStockKeepingUnit("TEST-123");
//        productRequest.setStockQuantity(10);

        productResponse = new ProductResponse();
        productResponse.setId(1L);
        productResponse.setName("Test Product");
        productResponse.setDescription("Test Description");
        productResponse.setPrice(new BigDecimal("99.99"));
        productResponse.setStockKeepingUnit("TEST-123");
        productResponse.setStockQuantity(10);

        authHeader = "Bearer test.jwt.token";
    }

    @Test
    @DisplayName("Should return all products successfully")
    void shouldReturnAllProducts_whenGetAllProducts() throws Exception {
        List<ProductResponse> products = Arrays.asList(productResponse);
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productResponse.getId()))
                .andExpect(jsonPath("$[0].name").value(productResponse.getName()));
    }

    @Test
    @DisplayName("Should return product by id successfully")
    void shouldReturnProduct_whenGetProductById() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productResponse);

        mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponse.getId()))
                .andExpect(jsonPath("$.name").value(productResponse.getName()));
    }

    @Test
    @DisplayName("Should create product successfully when admin")
    void shouldCreateProduct_whenAdmin() throws Exception {
        when(AuthService.isAdmin(authHeader)).thenReturn(true);
        when(productService.createProduct(any())).thenReturn(productResponse);

        mockMvc.perform(post("/products")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(productResponse.getId()))
                .andExpect(jsonPath("$.name").value(productResponse.getName()));
    }

    @Test
    @DisplayName("Should return unauthorized when no auth header")
    void shouldReturnUnauthorized_whenNoAuthHeader() throws Exception {
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Should return forbidden when not admin")
    void shouldReturnForbidden_whenNotAdmin() throws Exception {
        when(AuthService.isAdmin(authHeader)).thenReturn(false);

        mockMvc.perform(post("/products")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should update product successfully when admin")
    void shouldUpdateProduct_whenAdmin() throws Exception {
        when(AuthService.isAdmin(authHeader)).thenReturn(true);
        when(productService.updateProduct(anyLong(), any())).thenReturn(productResponse);

        mockMvc.perform(put("/products/1")
                .header("Authorization", authHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productResponse.getId()))
                .andExpect(jsonPath("$.name").value(productResponse.getName()));
    }

    @Test
    @DisplayName("Should delete product successfully when admin")
    void shouldDeleteProduct_whenAdmin() throws Exception {
        when(AuthService.isAdmin(authHeader)).thenReturn(true);

        mockMvc.perform(delete("/products/1")
                .header("Authorization", authHeader))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should find product by SKU successfully")
    void shouldFindProduct_whenSearchBySku() throws Exception {
        when(productService.getProductBySku(anyString())).thenReturn(productResponse);

        mockMvc.perform(get("/products/sku/TEST-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockKeepingUnit").value(productResponse.getStockKeepingUnit()));
    }

    @Test
    @DisplayName("Should add stock successfully when admin")
    void shouldAddStock_whenAdmin() throws Exception {
        when(AuthService.isAdmin(authHeader)).thenReturn(true);
        when(productService.addStock(anyString(), any())).thenReturn(productResponse);

        mockMvc.perform(put("/products/TEST-123/stock/add")
                .header("Authorization", authHeader)
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(productResponse.getStockQuantity()));
    }

    @Test
    @DisplayName("Should reduce stock successfully when admin")
    void shouldReduceStock_whenAdmin() throws Exception {
        when(AuthService.isAdmin(authHeader)).thenReturn(true);
        when(productService.reduceStock(anyString(), any())).thenReturn(productResponse);

        mockMvc.perform(put("/products/TEST-123/stock/reduce")
                .header("Authorization", authHeader)
                .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stockQuantity").value(productResponse.getStockQuantity()));
    }
}