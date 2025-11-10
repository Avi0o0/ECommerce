package com.ecom.orderservice.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;

import com.ecom.orderservice.entity.Order;
import com.ecom.orderservice.entity.OrderItem;
import com.ecom.orderservice.entity.OrderStatus;
import com.ecom.orderservice.entity.TestProduct;
import com.ecom.orderservice.entity.TestUser;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DisplayName("Order Repository Tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @BeforeEach
    void setUp() {
        // Create test users
        TestUser user1 = new TestUser(1L, "user1", "hashedpassword123");
        TestUser user2 = new TestUser(2L, "user2", "hashedpassword456");
        entityManager.persist(user1);
        entityManager.persist(user2);
        
        // Create test products
        TestProduct product1 = new TestProduct(1L, "Test Product 1", "Description 1", new BigDecimal("99.99"), 100, 1L, "SKU-001");
        TestProduct product2 = new TestProduct(2L, "Test Product 2", "Description 2", new BigDecimal("149.99"), 50, 1L, "SKU-002");
        entityManager.persist(product1);
        entityManager.persist(product2);
        
        entityManager.flush();
    }

    @Test
    @DisplayName("Should save order with items")
    void shouldSaveOrderWithItems() {
        // Given
        Order order = new Order();
        order.setUserId("1L");
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(1L);
        item.setQuantity(2);
        item.setPrice(new BigDecimal("99.99"));
        order.setOrderItems(List.of(item));

        // When
        Order savedOrder = orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        // Then
        Order found = orderRepository.findById(savedOrder.getId()).orElseThrow();
        assertNotNull(found);
        assertEquals(OrderStatus.PENDING, found.getOrderStatus());
        assertEquals(1, found.getOrderItems().size());
        OrderItem savedItem = found.getOrderItems().iterator().next();
        assertEquals(2, savedItem.getQuantity());
        assertEquals(0, new BigDecimal("99.99").compareTo(savedItem.getPrice()));
    }

    @Test
    @DisplayName("Should find orders by user ID")
    void shouldFindOrdersByUserId() {
        // Given
        Order order1 = new Order();
        order1.setUserId("1L");
        order1.setTotalAmount(new BigDecimal("199.98"));
        order1.setOrderStatus(OrderStatus.PENDING);
        order1.setPaymentStatus("PENDING");
        order1.setCreatedAt(LocalDateTime.now());
        entityManager.persist(order1);

        Order order2 = new Order();
        order2.setUserId("1L");
        order2.setTotalAmount(new BigDecimal("299.99"));
        order2.setOrderStatus(OrderStatus.COMPLETED);
        order2.setPaymentStatus("SUCCESS");
        order2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(order2);
        entityManager.flush();

        // When
        List<Order> found = orderRepository.findByUserIdOrderByCreatedAtDesc("1L");

        // Then
        assertEquals(2, found.size());
        assertTrue(found.stream().anyMatch(o -> o.getOrderStatus() == OrderStatus.PENDING));
        assertTrue(found.stream().anyMatch(o -> o.getOrderStatus() == OrderStatus.COMPLETED));
    }

    @Test
    @DisplayName("Should find order by ID and user ID")
    void shouldFindOrderByIdAndUserId() {
        // Given
        Order order = new Order();
        order.setUserId("1L");
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        entityManager.persist(order);
        entityManager.flush();

        // When
        Optional<Order> found = orderRepository.findByIdAndUserId(order.getId(), "1L");

        // Then
        assertTrue(found.isPresent());
        assertEquals(OrderStatus.PENDING, found.get().getOrderStatus());
    }

    @Test
    @DisplayName("Should update order status")
    void shouldUpdateOrderStatus() {
        // Given
        Order order = new Order();
        order.setUserId("1L");
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());
        entityManager.persist(order);
        entityManager.flush();

        // When
        order.setOrderStatus(OrderStatus.COMPLETED);
        order.setPaymentStatus("SUCCESS");
        orderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        // Then
        Order found = orderRepository.findById(order.getId()).orElseThrow();
        assertEquals(OrderStatus.COMPLETED, found.getOrderStatus());
        assertEquals("SUCCESS", found.getPaymentStatus());
    }

    @Test
    @DisplayName("Should cascade delete order items")
    void shouldCascadeDeleteOrderItems() {
        // Given
        Order order = new Order();
        order.setUserId("1L");
        order.setTotalAmount(new BigDecimal("199.98"));
        order.setOrderStatus(OrderStatus.PENDING);
        order.setPaymentStatus("PENDING");
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(1L);
        item.setQuantity(2);
        item.setPrice(new BigDecimal("99.99"));
        orderItems.add(item);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        entityManager.flush();

        // When
        orderRepository.deleteById(savedOrder.getId());
        entityManager.flush();
        entityManager.clear();

        // Then
        Optional<Order> foundOrder = orderRepository.findById(savedOrder.getId());
        assertTrue(foundOrder.isEmpty());

        Optional<OrderItem> foundItem = orderItemRepository.findById(item.getId());
        assertTrue(foundItem.isEmpty());
    }

    @Test
    @DisplayName("Should find orders by payment status")
    void shouldFindOrdersByPaymentStatus() {
        // First clean up any existing orders
        orderRepository.deleteAllInBatch();
        entityManager.flush();
        entityManager.clear();

        // Given
        Order order1 = new Order();
        order1.setUserId("1L");
        order1.setTotalAmount(new BigDecimal("199.98"));
        order1.setOrderStatus(OrderStatus.COMPLETED);
        order1.setPaymentStatus("SUCCESS");
        order1.setCreatedAt(LocalDateTime.now());
        entityManager.persist(order1);

        Order order2 = new Order();
        order2.setUserId("2L");
        order2.setTotalAmount(new BigDecimal("299.99"));
        order2.setOrderStatus(OrderStatus.PENDING);
        order2.setPaymentStatus("PENDING");
        order2.setCreatedAt(LocalDateTime.now());
        entityManager.persist(order2);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Order> successfulOrders = orderRepository.findByPaymentStatusOrderByCreatedAtDesc("SUCCESS");
        List<Order> pendingOrders = orderRepository.findByPaymentStatusOrderByCreatedAtDesc("PENDING");

        // Then
        assertEquals(1, successfulOrders.size());
        assertEquals(1, pendingOrders.size());
        assertEquals("SUCCESS", successfulOrders.get(0).getPaymentStatus());
        assertEquals("PENDING", pendingOrders.get(0).getPaymentStatus());
    }

    @Test
    @DisplayName("Should find orders between dates")
    void shouldFindOrdersBetweenDates() {
        // Given
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);

        Order oldOrder = new Order();
        oldOrder.setUserId("1L");
        oldOrder.setTotalAmount(new BigDecimal("199.98"));
        oldOrder.setOrderStatus(OrderStatus.COMPLETED);
        oldOrder.setPaymentStatus("SUCCESS");
        oldOrder.setCreatedAt(lastWeek);
        entityManager.persist(oldOrder);

        Order recentOrder = new Order();
        recentOrder.setUserId("1L");
        recentOrder.setTotalAmount(new BigDecimal("299.99"));
        recentOrder.setOrderStatus(OrderStatus.COMPLETED);
        recentOrder.setPaymentStatus("SUCCESS");
        recentOrder.setCreatedAt(LocalDateTime.now());
        entityManager.persist(recentOrder);
        entityManager.flush();

        // When
        List<Order> ordersInRange = orderRepository.findOrdersBetweenDates(yesterday, tomorrow);
        List<Order> ordersLastWeek = orderRepository.findOrdersBetweenDates(lastWeek.minusDays(1), lastWeek.plusDays(1));

        // Then
        assertEquals(1, ordersInRange.size());
        assertEquals(1, ordersLastWeek.size());
        assertEquals(recentOrder.getId(), ordersInRange.get(0).getId());
        assertEquals(oldOrder.getId(), ordersLastWeek.get(0).getId());
    }

    @Test
    @DisplayName("Should find pending orders older than cutoff time")
    void shouldFindPendingOrdersOlderThanCutoff() {
        // Given
        LocalDateTime oldTime = LocalDateTime.now().minusDays(7);
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        
        Order oldPendingOrder = new Order();
        oldPendingOrder.setUserId("1L");
        oldPendingOrder.setTotalAmount(new BigDecimal("199.98"));
        oldPendingOrder.setOrderStatus(OrderStatus.PENDING);
        oldPendingOrder.setPaymentStatus("PENDING");
        oldPendingOrder.setCreatedAt(oldTime);
        entityManager.persist(oldPendingOrder);

        Order recentPendingOrder = new Order();
        recentPendingOrder.setUserId("1L");
        recentPendingOrder.setTotalAmount(new BigDecimal("299.99"));
        recentPendingOrder.setOrderStatus(OrderStatus.PENDING);
        recentPendingOrder.setPaymentStatus("PENDING");
        recentPendingOrder.setCreatedAt(LocalDateTime.now());
        entityManager.persist(recentPendingOrder);
        entityManager.flush();

        // When
        List<Order> oldPendingOrders = orderRepository.findPendingOrdersOlderThan(cutoff);

        // Then
        assertEquals(1, oldPendingOrders.size());
        assertEquals(oldPendingOrder.getId(), oldPendingOrders.get(0).getId());
    }
}