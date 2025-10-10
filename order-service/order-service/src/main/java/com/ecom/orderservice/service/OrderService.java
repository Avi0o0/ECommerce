package com.ecom.orderservice.service;

import com.ecom.orderservice.client.CartServiceClient;
import com.ecom.orderservice.client.PaymentServiceClient;
import com.ecom.orderservice.client.ProductServiceClient;
import com.ecom.orderservice.client.InventoryServiceClient;
import com.ecom.orderservice.dto.*;
import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.entity.OrderItem;
import com.ecom.orderservice.entity.OrderStatus;
import com.ecom.orderservice.exception.*;
import com.ecom.orderservice.repository.OrderItemRepository;
import com.ecom.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductServiceClient productServiceClient;
    private final CartServiceClient cartServiceClient;
    private final PaymentServiceClient paymentServiceClient;
    private final InventoryServiceClient inventoryServiceClient;
    
    public OrderService(OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository,
                       ProductServiceClient productServiceClient,
                       CartServiceClient cartServiceClient,
                       PaymentServiceClient paymentServiceClient,
                       InventoryServiceClient inventoryServiceClient) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productServiceClient = productServiceClient;
        this.cartServiceClient = cartServiceClient;
        this.paymentServiceClient = paymentServiceClient;
        this.inventoryServiceClient = inventoryServiceClient;
    }
    
    /**
     * Place a new order from user's cart
     */
    public OrderDto.OrderResponse placeOrderFromCart(Long userId) {
        logger.info("Placing order for user: {}", userId);
        
        try {
            // 1. Fetch user's cart
            CartDto.CartResponse cart = cartServiceClient.getUserCart(userId).getBody();
            if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
                throw new OrderProcessingException("Cart is empty. Cannot place order.");
            }
            
            // 2. Validate products and check stock
            List<InventoryDto.ReserveRequest> stockReservations = new ArrayList<>();
            List<ProductDto.ProductResponse> products = new ArrayList<>();
            
            for (CartDto.CartItemResponse cartItem : cart.getCartItems()) {
                // Get product details
                ProductDto.ProductResponse product = productServiceClient.getProductById(cartItem.getProductId()).getBody();
                if (product == null || !product.getIsActive()) {
                    throw new OrderProcessingException("Product not found or inactive: " + cartItem.getProductId());
                }
                
                products.add(product);
                stockReservations.add(new InventoryDto.ReserveRequest(
                    cartItem.getProductId(), 
                    cartItem.getQuantity(), 
                    "cart-" + userId
                ));
            }
            
            // 3. Reserve stock
            for (InventoryDto.ReserveRequest reservation : stockReservations) {
                inventoryServiceClient.reserveStock(reservation);
            }
            
            // 4. Create order
            Order order = new Order(userId, cart.getTotalAmount());
            order = orderRepository.save(order);
            
            // 5. Create order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (int i = 0; i < cart.getCartItems().size(); i++) {
                CartDto.CartItemResponse cartItem = cart.getCartItems().get(i);
                
                OrderItem orderItem = new OrderItem(
                    order,
                    cartItem.getProductId(),
                    cartItem.getQuantity(),
                    cartItem.getPriceAtAddition()
                );
                orderItems.add(orderItem);
            }
            
            orderItemRepository.saveAll(orderItems);
            order.setOrderItems(orderItems);
            
            // 6. Clear user's cart
            cartServiceClient.clearUserCart(userId);
            
            logger.info("Order placed successfully with ID: {}", order.getId());
            return convertToOrderResponse(order, products);
            
        } catch (Exception e) {
            logger.error("Error placing order for user {}: {}", userId, e.getMessage(), e);
            throw new OrderProcessingException("Failed to place order: " + e.getMessage(), e);
        }
    }
    
    /**
     * Place a new order with specific items
     */
    public OrderDto.OrderResponse placeOrder(OrderDto.OrderRequest orderRequest) {
        logger.info("Placing order for user: {}", orderRequest.getUserId());
        
        try {
            // 1. Validate products and check stock
            List<InventoryDto.ReserveRequest> stockReservations = new ArrayList<>();
            List<ProductDto.ProductResponse> products = new ArrayList<>();
            
            for (OrderDto.OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
                // Get product details
                ProductDto.ProductResponse product = productServiceClient.getProductById(itemRequest.getProductId()).getBody();
                if (product == null || !product.getIsActive()) {
                    throw new OrderProcessingException("Product not found or inactive: " + itemRequest.getProductId());
                }
                
                products.add(product);
                stockReservations.add(new InventoryDto.ReserveRequest(
                    itemRequest.getProductId(), 
                    itemRequest.getQuantity(), 
                    "order-" + orderRequest.getUserId()
                ));
            }
            
            // 2. Reserve stock
            for (InventoryDto.ReserveRequest reservation : stockReservations) {
                inventoryServiceClient.reserveStock(reservation);
            }
            
            // 3. Calculate total amount
            BigDecimal totalAmount = BigDecimal.ZERO;
            for (int i = 0; i < orderRequest.getOrderItems().size(); i++) {
                OrderDto.OrderItemRequest itemRequest = orderRequest.getOrderItems().get(i);
                ProductDto.ProductResponse product = products.get(i);
                BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
            
            // 4. Create order
            Order order = new Order(orderRequest.getUserId(), totalAmount);
            order = orderRepository.save(order);
            
            // 5. Create order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (int i = 0; i < orderRequest.getOrderItems().size(); i++) {
                OrderDto.OrderItemRequest itemRequest = orderRequest.getOrderItems().get(i);
                ProductDto.ProductResponse product = products.get(i);
                
                OrderItem orderItem = new OrderItem(
                    order,
                    itemRequest.getProductId(),
                    itemRequest.getQuantity(),
                    product.getPrice()
                );
                orderItems.add(orderItem);
            }
            
            orderItemRepository.saveAll(orderItems);
            order.setOrderItems(orderItems);
            
            logger.info("Order placed successfully with ID: {}", order.getId());
            return convertToOrderResponse(order, products);
            
        } catch (Exception e) {
            logger.error("Error placing order for user {}: {}", orderRequest.getUserId(), e.getMessage(), e);
            throw new OrderProcessingException("Failed to place order: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update order status
     */
    public OrderDto.OrderResponse updateOrderStatus(Long orderId, String status) {
        logger.info("Updating order {} status to: {}", orderId, status);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        try {
            OrderStatus newStatus = OrderStatus.valueOf(status.toUpperCase());
            OrderStatus currentStatus = order.getStatus();
            
            // Validate status transition
            if (!isValidStatusTransition(currentStatus, newStatus)) {
                throw new InvalidOrderStatusException(currentStatus.toString(), newStatus.toString());
            }
            
            order.setStatus(newStatus);
            order = orderRepository.save(order);
            
            logger.info("Order {} status updated to: {}", orderId, status);
            return convertToOrderResponse(order, null);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid order status '{}' provided for order {}: {}", status, orderId, e.getMessage(), e);
            throw new InvalidOrderStatusException("Invalid status: " + status);
        }
    }
    
    /**
     * Cancel an order and restore stock
     */
    public OrderDto.OrderResponse cancelOrder(Long orderId) {
        logger.info("Canceling order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        if (order.getStatus() == OrderStatus.CANCELED) {
            throw new InvalidOrderStatusException("Order is already canceled");
        }
        
        if (order.getStatus() == OrderStatus.DELIVERED) {
            throw new InvalidOrderStatusException("Cannot cancel delivered order");
        }
        
        try {
            // Release reserved stock
            for (OrderItem orderItem : order.getOrderItems()) {
                InventoryDto.ReleaseRequest releaseRequest = new InventoryDto.ReleaseRequest(
                    orderItem.getProductId(), 
                    orderItem.getQuantity(), 
                    "order-" + order.getId()
                );
                inventoryServiceClient.releaseStock(releaseRequest);
            }
            
            // Update order status
            order.setStatus(OrderStatus.CANCELED);
            order = orderRepository.save(order);
            
            logger.info("Order {} canceled successfully", orderId);
            return convertToOrderResponse(order, null);
            
        } catch (Exception e) {
            logger.error("Error canceling order {}: {}", orderId, e.getMessage(), e);
            throw new OrderProcessingException("Failed to cancel order: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderDto.OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        return convertToOrderResponse(order, null);
    }
    
    /**
     * Get order by ID and user ID (for security)
     */
    @Transactional(readOnly = true)
    public OrderDto.OrderResponse getOrderByIdAndUserId(Long orderId, Long userId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        return convertToOrderResponse(order, null);
    }
    
    /**
     * Get order history by user ID
     */
    @Transactional(readOnly = true)
    public List<OrderDto.OrderResponse> getOrderHistoryByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
        return orders.stream()
            .map(order -> convertToOrderResponse(order, null))
            .toList();
    }
    
    /**
     * Get order history by user ID with pagination
     */
    @Transactional(readOnly = true)
    public Page<OrderDto.OrderResponse> getOrderHistoryByUserId(Long userId, Pageable pageable) {
        Page<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return orders.map(order -> convertToOrderResponse(order, null));
    }
    
    /**
     * Get orders by status
     */
    @Transactional(readOnly = true)
    public List<OrderDto.OrderResponse> getOrdersByStatus(String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderRepository.findByStatusOrderByCreatedAtDesc(orderStatus);
            return orders.stream()
                .map(order -> convertToOrderResponse(order, null))
                .toList();
        } catch (IllegalArgumentException e) {
            logger.error("Invalid order status '{}' provided for status filter: {}", status, e.getMessage(), e);
            throw new InvalidOrderStatusException("Invalid status: " + status);
        }
    }
    
    /**
     * Process payment for an order
     */
    public OrderDto.OrderResponse processPayment(Long orderId, String paymentMethod) {
        logger.info("Processing payment for order: {}", orderId);
        
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderNotFoundException(orderId));
        
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new InvalidOrderStatusException("Only pending orders can be paid");
        }
        
        try {
            // Create payment request
            PaymentDto.PaymentRequest paymentRequest = new PaymentDto.PaymentRequest(
                orderId,
                order.getUserId(),
                order.getTotalAmount(),
                paymentMethod,
                "USD"
            );
            
            // Process payment
            PaymentDto.PaymentResponse paymentResponse = paymentServiceClient.processPayment(paymentRequest).getBody();
            
            if (paymentResponse != null && "COMPLETED".equals(paymentResponse.getStatus())) {
                order.setStatus(OrderStatus.PAID);
                order = orderRepository.save(order);
                logger.info("Payment processed successfully for order: {}", orderId);
            } else {
                throw new OrderProcessingException("Payment processing failed");
            }
            
            return convertToOrderResponse(order, null);
            
        } catch (Exception e) {
            logger.error("Error processing payment for order {}: {}", orderId, e.getMessage(), e);
            throw new OrderProcessingException("Failed to process payment: " + e.getMessage(), e);
        }
    }
    
    // Helper methods
    
    private boolean isValidStatusTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        return switch (currentStatus) {
            case PENDING -> newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELED;
            case PAID -> newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELED;
            case SHIPPED -> newStatus == OrderStatus.DELIVERED;
            case DELIVERED, CANCELED -> false;
        };
    }
    
    private OrderDto.OrderResponse convertToOrderResponse(Order order, List<ProductDto.ProductResponse> products) {
        List<OrderDto.OrderItemResponse> orderItemResponses = new ArrayList<>();
        
        if (order.getOrderItems() != null) {
            for (OrderItem orderItem : order.getOrderItems()) {
                String productName = null;
                if (products != null) {
                    productName = products.stream()
                        .filter(p -> p.getId().equals(orderItem.getProductId()))
                        .findFirst()
                        .map(ProductDto.ProductResponse::getName)
                        .orElse(null);
                }
                
                OrderDto.OrderItemResponse itemResponse = new OrderDto.OrderItemResponse(
                    orderItem.getId(),
                    orderItem.getProductId(),
                    productName,
                    orderItem.getQuantity(),
                    orderItem.getPrice(),
                    orderItem.getTotalPrice(),
                    orderItem.getCreatedAt(),
                    orderItem.getUpdatedAt()
                );
                orderItemResponses.add(itemResponse);
            }
        }
        
        return new OrderDto.OrderResponse(
            order.getId(),
            order.getUserId(),
            order.getTotalAmount(),
            order.getStatus().toString(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            orderItemResponses
        );
    }
}
