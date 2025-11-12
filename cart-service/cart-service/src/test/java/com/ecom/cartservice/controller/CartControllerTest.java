//package com.ecom.cartservice.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import com.ecom.cartservice.dto.AddToCartRequest;
//import com.ecom.cartservice.dto.CartResponse;
//import com.ecom.cartservice.service.AuthenticationService;
//import com.ecom.cartservice.service.CartService;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//class CartControllerTest {
//
//	@Autowired
//	private MockMvc mockMvc;
//
//	@Autowired
//	private ObjectMapper objectMapper;
//
//	@Mock
//	private CartService cartService;
//
//	@Mock
//	private AuthenticationService authenticationService;
//
//	private AddToCartRequest addToCartRequest;
//	private CartResponse cartResponse;
//	private String authHeader;
//
//	@BeforeEach
//	void setUp() {
//		addToCartRequest = new AddToCartRequest();
//		addToCartRequest.setProductId(1L);
//		addToCartRequest.setQuantity(2);
//
//		CartResponse.CartItemResponse cartItemResponse = new CartResponse.CartItemResponse();
//		cartItemResponse.setId(1L);
//		cartItemResponse.setProductId(1L);
//		cartItemResponse.setQuantity(2);
//		cartItemResponse.setPriceAtAddition(new BigDecimal("99.99"));
//		cartItemResponse.setCurrentPrice(new BigDecimal("99.99"));
//		cartItemResponse.setTotalPrice(new BigDecimal("199.98"));
//		cartItemResponse.setAddedAt(LocalDateTime.now());
//
//		cartResponse = new CartResponse();
//		cartResponse.setId(1L);
//		cartResponse.setUserId("1L");
//		cartResponse.setTotalPrice(new BigDecimal("199.98"));
//		cartResponse.setItems(List.of(cartItemResponse));
//		cartResponse.setTotalItems(1);
//		cartResponse.setCreatedAt(LocalDateTime.now());
//
//		authHeader = "Bearer test.jwt.token";
//	}
//
//	@Test
//	@DisplayName("Should return cart by user ID when authorized")
//	void shouldReturnCart_whenAuthorized() throws Exception {
//		// Given
//		// when(authenticationService.isUser(authHeader)).thenReturn(true);
//		// when(authenticationService.getUserId(authHeader)).thenReturn("");
//		when(cartService.getCart("")).thenReturn(cartResponse);
//
//		// When/Then
//		mockMvc.perform(get("/cart").header("Authorization", authHeader).param("userId", "1"))
//				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(cartResponse.getId()))
//				.andExpect(jsonPath("$.userId").value(cartResponse.getUserId()));
//	}
//
//	@Test
//	@DisplayName("Should add item to cart when authorized")
//	void shouldAddItemToCart_whenAuthorized() throws Exception {
//		// Given
//		// when(authenticationService.isUser(authHeader)).thenReturn(true);
//		// when(authenticationService.getUserId(authHeader)).thenReturn("");
//		when(cartService.addToCart(anyString(), any(AddToCartRequest.class))).thenReturn(cartResponse);
//
//		// When/Then
//		mockMvc.perform(post("/cart/add").param("userId", "1").header("Authorization", authHeader)
//				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(addToCartRequest)))
//				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(cartResponse.getId()));
//	}
//
//	@Test
//	@DisplayName("Should return unauthorized when no auth header")
//	void shouldReturnUnauthorized_whenNoAuthHeader() throws Exception {
//		mockMvc.perform(post("/cart/add").param("userId", "1").contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper.writeValueAsString(addToCartRequest))).andExpect(status().isUnauthorized());
//	}
//
//	@Test
//	@DisplayName("Should return forbidden when not user")
//	void shouldReturnForbidden_whenNotUser() throws Exception {
//		// Given
//		// when(authenticationService.isUser(authHeader)).thenReturn(false);
//
//		// When/Then
//		mockMvc.perform(post("/cart/add").param("userId", "1").header("Authorization", authHeader)
//				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(addToCartRequest)))
//				.andExpect(status().isForbidden());
//	}
//
//	@Test
//	@DisplayName("Should return forbidden when accessing another user's cart")
//	void shouldReturnForbidden_whenAccessingOtherUserCart() throws Exception {
//		// Given
//		// when(authenticationService.isUser(authHeader)).thenReturn(true);
//		// when(authenticationService.getUserId(authHeader)).thenReturn("1L");
//
//		// When/Then
//		mockMvc.perform(post("/cart/add").param("userId", "1").header("Authorization", authHeader)
//				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(addToCartRequest)))
//				.andExpect(status().isForbidden());
//	}
//
//	@Test
//	@DisplayName("Should update cart item by adding again when authorized")
//	void shouldUpdateCartItem_whenAuthorized() throws Exception {
//		// Given
//		// when(authenticationService.isUser(authHeader)).thenReturn(true);
//		// when(authenticationService.getUserId(authHeader)).thenReturn("2L");
//		when(cartService.addToCart(anyString(), any(AddToCartRequest.class))).thenReturn(cartResponse);
//
//		// When/Then
//		mockMvc.perform(post("/cart/add").header("Authorization", authHeader).param("userId", "1")
//				.contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(addToCartRequest)))
//				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(cartResponse.getId()));
//	}
//
//	@Test
//	@DisplayName("Should remove item from cart when authorized")
//	void shouldRemoveItemFromCart_whenAuthorized() throws Exception {
//		// Given
//		// when(authenticationService.isUser(authHeader)).thenReturn(true);
//		// when(authenticationService.getUserId(authHeader)).thenReturn("1L");
//		when(cartService.removeFromCart(anyString(), anyLong())).thenReturn(cartResponse);
//
//		// When/Then
//		mockMvc.perform(delete("/cart/remove/{productId}", 1L).header("Authorization", authHeader).param("userId", "1"))
//				.andExpect(status().isOk()).andExpect(jsonPath("$.id").value(cartResponse.getId()));
//	}
//
//	@Test
//	@DisplayName("Should clear cart when authorized")
//	void shouldClearCart_whenAuthorized() throws Exception {
//		// Given
//		// when(authenticationService.isUser(authHeader)).thenReturn(true);
//		// when(authenticationService.getUserId(authHeader)).thenReturn("1L");
//		when(cartService.getCart(anyString())).thenReturn(cartResponse);
//
//		// When/Then
//		mockMvc.perform(delete("/cart/clear").header("Authorization", authHeader).param("userId", "1L")
//				.param("paymentMethod", "CC").requestAttr("isUser", true)).andExpect(status().isOk());
//	}
//}