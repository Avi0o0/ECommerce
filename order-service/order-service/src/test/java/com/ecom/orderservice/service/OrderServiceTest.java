package com.ecom.orderservice.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecom.orderservice.client.PaymentClient;
import com.ecom.orderservice.client.ProductServiceClient;
import com.ecom.orderservice.dto.OrderItemRequest;
import com.ecom.orderservice.dto.OrderRequest;
import com.ecom.orderservice.dto.OrderResponse;
import com.ecom.orderservice.dto.PaymentRequest;
import com.ecom.orderservice.dto.PaymentResponse;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.entity.OrderItem;
import com.ecom.orderservice.entity.OrderStatus;
import com.ecom.orderservice.exception.OrderNotFoundException;
import com.ecom.orderservice.repository.OrderItemRepository;
import com.ecom.orderservice.repository.OrderRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private ProductServiceClient productServiceClient;

    @InjectMocks
    private OrderService orderService;

    private OrderRequest orderRequest;
    private Order savedOrder;
    private OrderItem orderItem;

    @BeforeEach
    void setUp() {
        // Setup Order Request
        orderRequest = new OrderRequest();
        orderRequest.setUserId("1L");
        orderRequest.setTotalAmount(new BigDecimal("199.98"));
        orderRequest.setPaymentMethod("CREDIT_CARD");
        
        OrderItemRequest itemRequest = new OrderItemRequest();
        itemRequest.setProductId(100L);
        itemRequest.setQuantity(2);
        itemRequest.setPrice(new BigDecimal("99.99"));
        
        orderRequest.setOrderItems(Arrays.asList(itemRequest));

        // Setup Saved Order
        savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId("1L");
        savedOrder.setTotalAmount(new BigDecimal("199.98"));
        savedOrder.setOrderStatus(OrderStatus.PENDING);
        savedOrder.setPaymentStatus("PENDING");
        savedOrder.setCreatedAt(LocalDateTime.now());

        // Setup Order Item
        orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setOrder(savedOrder);
        orderItem.setProductId(100L);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("99.99"));
    }

    // Test: Checkout
    @Test
    @DisplayName("Should complete checkout with successful payment")
    void testCheckout_SuccessfulPayment() {
        // Arrange
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setId(1L);
        paymentResponse.setOrderId(1L);
        paymentResponse.setUserId("1L");
        paymentResponse.setAmount(new BigDecimal("199.98"));
        paymentResponse.setPaymentStatus("SUCCESS");
        paymentResponse.setTransactionId("TXN123");
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            if (order.getOrderStatus() == OrderStatus.PENDING) {
                return order;
            }
            savedOrder.setOrderStatus(order.getOrderStatus());
            savedOrder.setPaymentStatus(order.getPaymentStatus());
            return savedOrder;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(orderItem));
        doNothing().when(productServiceClient).reduceStockByProductId(anyLong(), anyInt());

        // Act
        OrderResponse result = orderService.checkout(orderRequest);

        // Assert
        assertNotNull(result);
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(paymentClient, times(1)).processPayment(any(PaymentRequest.class));
        verify(productServiceClient, atLeastOnce()).reduceStockByProductId(anyLong(), anyInt());
    }

    @Test
    @DisplayName("Should handle payment failure gracefully")
    void testCheckout_PaymentFailure() {
        // Arrange
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentStatus("FAILED");
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            if (order.getOrderStatus() == OrderStatus.FAILED) {
                savedOrder.setOrderStatus(OrderStatus.FAILED);
                savedOrder.setPaymentStatus("FAILED");
                return savedOrder;
            }
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        // Act
        OrderResponse result = orderService.checkout(orderRequest);

        // Assert
        assertNotNull(result);
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
        verify(paymentClient, times(1)).processPayment(any(PaymentRequest.class));
    }

    @Test
    @DisplayName("Should reduce stock after successful payment")
    void testCheckout_StockReduction() {
        // Arrange
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentStatus("SUCCESS");
        
        OrderItem item1 = new OrderItem();
        item1.setProductId(100L);
        item1.setQuantity(2);
        
        OrderItem item2 = new OrderItem();
        item2.setProductId(200L);
        item2.setQuantity(1);
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            if (order.getOrderStatus() == OrderStatus.COMPLETED) {
                savedOrder.setOrderStatus(OrderStatus.COMPLETED);
                savedOrder.setPaymentStatus("SUCCESS");
                return savedOrder;
            }
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(item1, item2));
        doNothing().when(productServiceClient).reduceStockByProductId(anyLong(), anyInt());

        // Act
        orderService.checkout(orderRequest);

        // Assert
        verify(productServiceClient, times(1)).reduceStockByProductId(100L, 2);
        verify(productServiceClient, times(1)).reduceStockByProductId(200L, 1);
    }

    @Test
    @DisplayName("Should handle payment service unavailable")
    void testCheckout_PaymentServiceUnavailable() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            if (order.getOrderStatus() == OrderStatus.INCOMPLETE) {
                savedOrder.setOrderStatus(OrderStatus.INCOMPLETE);
                savedOrder.setPaymentStatus("PENDING");
                return savedOrder;
            }
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        when(paymentClient.processPayment(any(PaymentRequest.class)))
            .thenThrow(new RuntimeException("Payment service unavailable"));
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        // Act
        OrderResponse result = orderService.checkout(orderRequest);

        // Assert
        assertNotNull(result);
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
    }

    @Test
    @DisplayName("Should handle exception during checkout")
    void testCheckout_ExceptionHandling() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class)))
            .thenThrow(new RuntimeException("Database error"));
        when(orderRepository.findByUserIdOrderByCreatedAtDesc("1L"))
            .thenReturn(Arrays.asList(savedOrder));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> orderService.checkout(orderRequest));
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
    }

    // Test: Get Order by ID
    @Test
    @DisplayName("Should return order when valid ID is provided")
    void testGetOrderById_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(savedOrder));

        // Act
        OrderResponse result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(new BigDecimal("199.98"), result.getTotalAmount());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when order not found")
    void testGetOrderById_NotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrderById(999L));
        verify(orderRepository, times(1)).findById(999L);
    }

    // Test: Get Orders by User ID
    @Test
    @DisplayName("Should return all orders for a user")
    void testGetOrdersByUserId_Success() {
        // Arrange
        Order order1 = createOrder(1L, "100L", "100.00");
        Order order2 = createOrder(2L, "100L", "200.00");
        List<Order> orders = Arrays.asList(order1, order2);
        
        when(orderRepository.findByUserIdOrderByCreatedAtDesc("100L")).thenReturn(orders);

        // Act
        List<OrderResponse> result = orderService.getOrdersByUserId("100L");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals(2L, result.get(1).getId());
        verify(orderRepository, times(1)).findByUserIdOrderByCreatedAtDesc("100L");
    }

    @Test
    @DisplayName("Should return empty list when user has no orders")
    void testGetOrdersByUserId_EmptyList() {
        // Arrange
        when(orderRepository.findByUserIdOrderByCreatedAtDesc("999L"))
            .thenReturn(new ArrayList<>());

        // Act
        List<OrderResponse> result = orderService.getOrdersByUserId("999L");

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(orderRepository, times(1)).findByUserIdOrderByCreatedAtDesc("999L");
    }

    @Test
    @DisplayName("Should return orders ordered by created date descending")
    void testGetOrdersByUserId_OrderedByDate() {
        // Arrange
        Order order1 = createOrder(1L, "100L", "100.00");
        order1.setCreatedAt(LocalDateTime.now().minusDays(2));
        
        Order order2 = createOrder(2L, "100L", "200.00");
        order2.setCreatedAt(LocalDateTime.now().minusDays(1));
        
        List<Order> orders = Arrays.asList(order2, order1);
        
        when(orderRepository.findByUserIdOrderByCreatedAtDesc("100L")).thenReturn(orders);

        // Act
        List<OrderResponse> result = orderService.getOrdersByUserId("100L");

        // Assert
        assertEquals(2, result.size());
        assertEquals(2L, result.get(0).getId());
        assertEquals(1L, result.get(1).getId());
        verify(orderRepository, times(1)).findByUserIdOrderByCreatedAtDesc("100L");
    }

    // Test: Payment Processing
    @Test
    @DisplayName("Should create payment request with correct details")
    void testCheckout_PaymentRequestDetails() {
        // Arrange
        ArgumentCaptor<PaymentRequest> captor = ArgumentCaptor.forClass(PaymentRequest.class);
        
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentStatus("SUCCESS");
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            if (order.getOrderStatus() == OrderStatus.COMPLETED) {
                savedOrder.setOrderStatus(OrderStatus.COMPLETED);
                savedOrder.setPaymentStatus("SUCCESS");
                return savedOrder;
            }
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        when(paymentClient.processPayment(captor.capture())).thenReturn(paymentResponse);

        // Act
        orderService.checkout(orderRequest);

        // Assert
        PaymentRequest paymentRequest = captor.getValue();
        assertNotNull(paymentRequest);
        assertEquals(1L, paymentRequest.getOrderId());
        assertEquals(1L, paymentRequest.getUserId());
        assertEquals(new BigDecimal("199.98"), paymentRequest.getAmount());
        assertEquals("CREDIT_CARD", paymentRequest.getPaymentMethod());
    }

    @Test
    @DisplayName("Should create order items with correct details")
    void testCheckout_OrderItemsCreation() {
        // Arrange
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentStatus("SUCCESS");
        
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            if (order.getOrderStatus() == OrderStatus.COMPLETED) {
                savedOrder.setOrderStatus(OrderStatus.COMPLETED);
                savedOrder.setPaymentStatus("SUCCESS");
                return savedOrder;
            }
            return order;
        });
        
        ArgumentCaptor<OrderItem> captor = ArgumentCaptor.forClass(OrderItem.class);
        when(orderItemRepository.save(captor.capture())).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);
        when(orderItemRepository.findByOrderId(1L)).thenReturn(Arrays.asList(orderItem));
        doNothing().when(productServiceClient).reduceStockByProductId(anyLong(), anyInt());

        // Act
        orderService.checkout(orderRequest);

        // Assert
        assertEquals(1, captor.getAllValues().size());
        OrderItem savedItem = captor.getValue();
        assertEquals(100L, savedItem.getProductId());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(new BigDecimal("99.99"), savedItem.getPrice());
    }

    // Test: Order Status Transitions
    @Test
    @DisplayName("Should update order status to FAILED when payment fails")
    void testProcessPayment_OrderStatusFailed() {
        // Arrange
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentStatus("FAILED");
        
        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        when(orderRepository.save(orderCaptor.capture())).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(1L);
            if (order.getOrderStatus() == OrderStatus.FAILED) {
                savedOrder.setOrderStatus(OrderStatus.FAILED);
                savedOrder.setPaymentStatus("FAILED");
            }
            return order;
        });
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            item.setId(1L);
            return item;
        });
        when(paymentClient.processPayment(any(PaymentRequest.class))).thenReturn(paymentResponse);

        // Act
        OrderResponse result = orderService.checkout(orderRequest);

        // Assert
        assertNotNull(result);
        assertTrue(orderCaptor.getAllValues().stream()
            .anyMatch(o -> o.getOrderStatus() == OrderStatus.FAILED));
    }

    // Helper Methods
    private Order createOrder(Long id, String userId, String amount) {
        Order order = new Order();
        order.setId(id);
        order.setUserId(userId);
        order.setTotalAmount(new BigDecimal(amount));
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setPaymentStatus("SUCCESS");
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }
}

