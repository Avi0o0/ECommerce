package com.ecom.orderservice.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecom.orderservice.client.PaymentClient;
import com.ecom.orderservice.client.ProductServiceClient;
import com.ecom.orderservice.constants.OrderServiceConstants;
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

@Service
@Transactional
public class OrderService {

	private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final PaymentClient paymentClient;
	private final ProductServiceClient productServiceClient;

	public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
			PaymentClient paymentClient, ProductServiceClient productServiceClient) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
		this.paymentClient = paymentClient;
		this.productServiceClient = productServiceClient;
	}

	@Transactional
	public OrderResponse checkout(OrderRequest request) {
		logger.info(OrderServiceConstants.LOG_STARTING_CHECKOUT_PROCESS, request.getUserId());

		try {
			// Create order with PENDING status
			Order order = new Order();
			order.setUserId(request.getUserId());
			order.setTotalAmount(request.getTotalAmount());
			order.setOrderStatus(OrderStatus.PENDING);
			order.setPaymentStatus(OrderServiceConstants.PAYMENT_STATUS_PENDING);

			Order savedOrder = orderRepository.save(order);
			logger.info(OrderServiceConstants.LOG_ORDER_CREATED_WITH_STATUS, savedOrder.getId());
			logger.info("Order {} successfully saved to database with ID: {}", savedOrder.getId(), savedOrder.getId());

			// Create order items
			for (OrderItemRequest itemRequest : request.getOrderItems()) {
				OrderItem orderItem = new OrderItem();
				orderItem.setOrder(savedOrder);
				orderItem.setProductId(itemRequest.getProductId());
				orderItem.setQuantity(itemRequest.getQuantity());
				orderItem.setPrice(itemRequest.getPrice());

				orderItemRepository.save(orderItem);
			}
			logger.info(OrderServiceConstants.LOG_ORDER_ITEMS_CREATED, savedOrder.getId());

			// Process payment
			PaymentRequest paymentRequest = new PaymentRequest(savedOrder.getId(), request.getUserId(),
					request.getTotalAmount(), request.getPaymentMethod());

			logger.info(OrderServiceConstants.LOG_CALLING_PAYMENT_SERVICE, savedOrder.getId());
			logger.info("About to call Payment Service with order ID: {} for user: {}", savedOrder.getId(),
					request.getUserId());

			return processPayment(paymentRequest, savedOrder);

		} catch (Exception e) {
			logger.error(OrderServiceConstants.LOG_ERROR_DURING_CHECKOUT, request.getUserId(), e.getMessage(), e);

			// If an order was created but payment failed, mark it as FAILED
			try {
				// Find the most recent order for this user that might have been created
				List<Order> recentOrders = orderRepository.findByUserIdOrderByCreatedAtDesc(request.getUserId());
				if (!recentOrders.isEmpty()) {
					Order recentOrder = recentOrders.get(0);
					// Only update if the order is still PENDING (not already processed)
					if (recentOrder.getOrderStatus() == OrderStatus.PENDING) {
						recentOrder.setOrderStatus(OrderStatus.FAILED);
						recentOrder.setPaymentStatus(OrderServiceConstants.PAYMENT_STATUS_FAILED);
						orderRepository.save(recentOrder);
						logger.info("Marked order {} as FAILED due to checkout error", recentOrder.getId());
					}
				}
			} catch (Exception ex) {
				logger.error(OrderServiceConstants.LOG_ERROR_UPDATING_ORDER_STATUS, ex.getMessage());
			}

			throw new RuntimeException(OrderServiceConstants.CHECKOUT_FAILED_MESSAGE + ": " + e.getMessage(), e);
		}
	}

	private OrderResponse processPayment(PaymentRequest paymentRequest, Order savedOrder) {
		try {
			PaymentResponse paymentResponse = paymentClient.processPayment(paymentRequest);

			// Update order based on payment response
			if (paymentResponse != null && OrderServiceConstants.PAYMENT_RESPONSE_SUCCESS
					.equalsIgnoreCase(paymentResponse.getPaymentStatus())) {
				savedOrder.setOrderStatus(OrderStatus.COMPLETED);
				savedOrder.setPaymentStatus(OrderServiceConstants.PAYMENT_STATUS_SUCCESS);
				logger.info(OrderServiceConstants.LOG_PAYMENT_SUCCESSFUL, savedOrder.getId(), paymentResponse.getId());

				// Reduce stock after successful payment
				reduceStockForOrder(savedOrder);
			} else {
				savedOrder.setOrderStatus(OrderStatus.FAILED);
				savedOrder.setPaymentStatus(OrderServiceConstants.PAYMENT_STATUS_FAILED);
				logger.warn(OrderServiceConstants.LOG_PAYMENT_FAILED, savedOrder.getId());
			}

			Order updatedOrder = orderRepository.save(savedOrder);
			logger.info(OrderServiceConstants.LOG_ORDER_STATUS_UPDATED, updatedOrder.getOrderStatus(),
					updatedOrder.getId());

			return convertToResponse(updatedOrder);

		} catch (Exception paymentException) {
			logger.warn(OrderServiceConstants.LOG_PAYMENT_SERVICE_UNAVAILABLE, savedOrder.getId());
			logger.warn("Payment service error: {}", paymentException.getMessage());

			// Mark order as INCOMPLETE when payment service is unavailable
			savedOrder.setOrderStatus(OrderStatus.INCOMPLETE);
			savedOrder.setPaymentStatus(OrderServiceConstants.PAYMENT_STATUS_PENDING);

			Order updatedOrder = orderRepository.save(savedOrder);
			logger.info(OrderServiceConstants.LOG_ORDER_STATUS_UPDATED, updatedOrder.getOrderStatus(),
					updatedOrder.getId());

			// Fetch the order with its items from database to ensure orderItems are loaded
			Order orderWithItems = orderRepository.findById(updatedOrder.getId())
					.orElseThrow(() -> new OrderNotFoundException(
							OrderServiceConstants.ORDER_NOT_FOUND_MESSAGE + updatedOrder.getId()));

			// Return the incomplete order response instead of throwing exception
			return convertToResponse(orderWithItems);
		}
	}

	@Transactional(readOnly = true)
	public OrderResponse getOrderById(Long orderId) {
		logger.info(OrderServiceConstants.LOG_GETTING_ORDER_BY_ID, orderId);
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new OrderNotFoundException(OrderServiceConstants.ORDER_NOT_FOUND_MESSAGE + orderId));
		return convertToResponse(order);
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> getOrdersByUserId(Long userId) {
		logger.info(OrderServiceConstants.LOG_GETTING_ORDERS_FOR_USER, userId);
		List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
		return orders.stream().map(this::convertToResponse).toList();
	}

	private OrderResponse convertToResponse(Order order) {
		return new OrderResponse(order.getId(), order.getUserId(), order.getTotalAmount(), order.getOrderStatus(),
				order.getPaymentStatus(), order.getCreatedAt());
	}

	/**
	 * Reduce stock for all products in the order after successful payment
	 */
	private void reduceStockForOrder(Order order) {
		logger.info("Reducing stock for order ID: {}", order.getId());

		try {
			// Get all order items for this order
			List<OrderItem> orderItems = orderItemRepository.findByOrderId(order.getId());

			for (OrderItem item : orderItems) {
				try {
					logger.info("Reducing stock for product ID: {} by quantity: {}", item.getProductId(),
							item.getQuantity());
					productServiceClient.reduceStockByProductId(item.getProductId(), item.getQuantity());
					logger.info("Successfully reduced stock for product ID: {}", item.getProductId());
				} catch (Exception e) {
					logger.error("Failed to reduce stock for product ID: {} - Error: {}", item.getProductId(),
							e.getMessage(), e);
					// Continue processing other products even if one fails
				}
			}

			logger.info("Stock reduction completed for order ID: {}", order.getId());
		} catch (Exception e) {
			logger.error("Error reducing stock for order ID: {} - Error: {}", order.getId(), e.getMessage(), e);
			// Don't throw exception - order is already marked as completed
		}
	}
}