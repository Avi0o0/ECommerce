package com.ecom.cartservice.service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecom.cartservice.client.OrderServiceClient;
import com.ecom.cartservice.client.ProductServiceClient;
import com.ecom.cartservice.dto.AddToCartRequest;
import com.ecom.cartservice.dto.CartResponse;
import com.ecom.cartservice.dto.OrderResponse;
import com.ecom.cartservice.dto.ProductDto;
import com.ecom.cartservice.entity.Cart;
import com.ecom.cartservice.entity.CartItem;
import com.ecom.cartservice.exception.CartItemNotFoundException;
import com.ecom.cartservice.exception.CartNotFoundException;
import com.ecom.cartservice.exception.ProductNotAvailableException;
import com.ecom.cartservice.repository.CartItemRepository;
import com.ecom.cartservice.repository.CartRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("CartService Tests")
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private OrderServiceClient orderServiceClient;

    @InjectMocks
    private CartService cartService;

    private Cart cart;
    private CartItem cartItem;
    private ProductDto.ProductResponse productResponse;
    private AddToCartRequest addToCartRequest;

    @BeforeEach
    void setUp() {
        // Setup Cart
        cart = new Cart();
        cart.setId(1L);
        cart.setUserId(1L);

        // Setup CartItem
        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProductId(100L);
        cartItem.setQuantity(2);
        cartItem.setPriceAtAddition(new BigDecimal("99.99"));
        cartItem.setCart(cart);
        cart.getCartItems().add(cartItem);

        // Setup Product Response
        productResponse = new ProductDto.ProductResponse();
        productResponse.setId(100L);
        productResponse.setName("Test Product");
        productResponse.setDescription("Test Description");
        productResponse.setPrice(new BigDecimal("99.99"));
        productResponse.setStockQuantity(50);

        // Setup Add to Cart Request
        addToCartRequest = new AddToCartRequest();
        addToCartRequest.setProductId(100L);
        addToCartRequest.setQuantity(1);
    }

    // Test: Get Cart
    @Test
    @DisplayName("Should return cart when user has cart")
    void testGetCart_Success() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productServiceClient.getProductById(100L)).thenReturn(productResponse);

        // Act
        CartResponse result = cartService.getCart(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        verify(cartRepository, times(1)).findByUserId(1L);
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when cart doesn't exist")
    void testGetCart_NotFound() {
        // Arrange
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> cartService.getCart(999L));
        verify(cartRepository, times(1)).findByUserId(999L);
    }

    // Test: Add to Cart
    @Test
    @DisplayName("Should add new item to cart successfully")
    void testAddToCart_NewItem() {
        // Arrange
        Cart emptyCart = new Cart();
        emptyCart.setId(1L);
        emptyCart.setUserId(1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(emptyCart));
        lenient().when(productServiceClient.getProductById(100L)).thenReturn(productResponse);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartResponse result = cartService.addToCart(1L, addToCartRequest);

        // Assert
        assertNotNull(result);
        verify(cartItemRepository, atLeastOnce()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should update quantity for existing item in cart")
    void testAddToCart_UpdateQuantity() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        lenient().when(productServiceClient.getProductById(100L)).thenReturn(productResponse);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartResponse result = cartService.addToCart(1L, addToCartRequest);

        // Assert
        assertNotNull(result);
        verify(cartItemRepository, atLeastOnce()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw ProductNotAvailableException when product doesn't exist")
    void testAddToCart_ProductNotFound() {
        // Arrange
        lenient().when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(new Cart()));
        lenient().when(productServiceClient.getProductById(999L)).thenReturn(null);

        // Act & Assert
        assertThrows(ProductNotAvailableException.class, () -> {
            addToCartRequest.setProductId(999L);
            cartService.addToCart(1L, addToCartRequest);
        });
    }

    @Test
    @DisplayName("Should throw ProductNotAvailableException when insufficient stock")
    void testAddToCart_InsufficientStock() {
        // Arrange
        productResponse.setStockQuantity(0);
        lenient().when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(new Cart()));
        lenient().when(productServiceClient.getProductById(100L)).thenReturn(productResponse);

        // Act & Assert
        assertThrows(ProductNotAvailableException.class, () -> 
            cartService.addToCart(1L, addToCartRequest));
    }

    // Test: Remove from Cart
    @Test
    @DisplayName("Should remove item from cart successfully")
    void testRemoveFromCart_Success() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        doNothing().when(cartItemRepository).delete(any(CartItem.class));

        // Act
        CartResponse result = cartService.removeFromCart(1L, 100L);

        // Assert
        assertNotNull(result);
        verify(cartItemRepository, times(1)).delete(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when cart doesn't exist for removal")
    void testRemoveFromCart_CartNotFound() {
        // Arrange
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> cartService.removeFromCart(999L, 100L));
        verify(cartRepository, times(1)).findByUserId(999L);
    }

    @Test
    @DisplayName("Should throw CartItemNotFoundException when item doesn't exist in cart")
    void testRemoveFromCart_ItemNotFound() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        // Act & Assert
        assertThrows(CartItemNotFoundException.class, () -> cartService.removeFromCart(1L, 999L));
    }

    // Test: Clear Cart
    @Test
    @DisplayName("Should clear cart successfully")
    void testClearCart_Success() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        doNothing().when(cartItemRepository).deleteAll(any());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        cartService.clearCart(1L);

        // Assert
        verify(cartItemRepository, times(1)).deleteAll(any());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when clearing non-existent cart")
    void testClearCart_NotFound() {
        // Arrange
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> cartService.clearCart(999L));
        verify(cartRepository, times(1)).findByUserId(999L);
    }

    // Test: Checkout
    @Test
    @DisplayName("Should complete checkout successfully")
    void testCheckout_Success() {
        // Arrange
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setId(1L);
        orderResponse.setUserId(1L);
        orderResponse.setTotalAmount(new BigDecimal("199.98"));
        orderResponse.setOrderStatus("COMPLETED");
        orderResponse.setPaymentStatus("SUCCESS");

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(orderServiceClient.checkout(any(), eq("Bearer token"))).thenReturn(orderResponse);
        doNothing().when(cartItemRepository).deleteAll(any());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        // Act
        OrderResponse result = cartService.checkout(1L, "CREDIT_CARD", "Bearer token");

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getOrderStatus());
        verify(orderServiceClient, times(1)).checkout(any(), eq("Bearer token"));
        verify(cartItemRepository, times(1)).deleteAll(any());
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when checking out non-existent cart")
    void testCheckout_CartNotFound() {
        // Arrange
        when(cartRepository.findByUserId(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> 
            cartService.checkout(999L, "CREDIT_CARD", "Bearer token"));
        verify(cartRepository, times(1)).findByUserId(999L);
        verify(orderServiceClient, never()).checkout(any(), any());
    }

    @Test
    @DisplayName("Should throw CartNotFoundException when checking out empty cart")
    void testCheckout_EmptyCart() {
        // Arrange
        Cart emptyCart = new Cart();
        emptyCart.setId(1L);
        emptyCart.setUserId(1L);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(emptyCart));

        // Act & Assert
        assertThrows(CartNotFoundException.class, () -> 
            cartService.checkout(1L, "CREDIT_CARD", "Bearer token"));
        verify(orderServiceClient, never()).checkout(any(), any());
    }

    // Test: Create New Cart
    @Test
    @DisplayName("Should create new cart when adding item to non-existent cart")
    void testAddToCart_CreateNewCart() {
        // Arrange
        when(cartRepository.findByUserId(2L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart newCart = invocation.getArgument(0);
            newCart.setId(2L);
            return newCart;
        });
        when(productServiceClient.getProductById(100L)).thenReturn(productResponse);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        CartResponse result = cartService.addToCart(2L, addToCartRequest);

        // Assert
        assertNotNull(result);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartItemRepository, atLeastOnce()).save(any(CartItem.class));
    }

    @Test
    @DisplayName("Should throw exception when adding item with null product price")
    void testAddToCart_NullProductPrice() {
        // Arrange
        ProductDto.ProductResponse nullPriceProduct = new ProductDto.ProductResponse();
        nullPriceProduct.setId(100L);
        nullPriceProduct.setName("Test Product");
        nullPriceProduct.setDescription("Test Description");
        nullPriceProduct.setStockQuantity(50);
        nullPriceProduct.setPrice(null);

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(new Cart()));
        when(productServiceClient.getProductById(100L)).thenReturn(nullPriceProduct);

        // Act & Assert
        assertThrows(ProductNotAvailableException.class, () -> cartService.addToCart(1L, addToCartRequest));
    }

    @Test
    @DisplayName("Should handle exception when getting product details during cart conversion")
    void testGetCart_ProductServiceError() {
        // Arrange
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productServiceClient.getProductById(100L)).thenThrow(new RuntimeException("Product service unavailable"));

        // Act
        CartResponse result = cartService.getCart(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertFalse(result.getItems().isEmpty());
        CartResponse.CartItemResponse item = result.getItems().get(0);
        assertTrue(item.getProductName().startsWith("Product"));
        assertEquals("Product details are temporarily unavailable", item.getProductDescription());
        verify(productServiceClient, times(1)).getProductById(100L);
    }
}

