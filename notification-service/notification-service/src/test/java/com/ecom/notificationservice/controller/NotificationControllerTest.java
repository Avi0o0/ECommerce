package com.ecom.notificationservice.controller;

import com.ecom.notificationservice.dto.NotificationRequest;
import com.ecom.notificationservice.dto.NotificationResponse;
import com.ecom.notificationservice.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "eureka.client.enabled=false"
})
@DisplayName("Notification Controller Tests")
class NotificationControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private NotificationService notificationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Should send notification successfully")
    void testSendNotification() throws Exception {
        // Arrange
        NotificationRequest request = new NotificationRequest();
        request.setUserId(1L);
        request.setType("ORDER_CONFIRMATION");
        request.setMessage("Your order has been confirmed");

        NotificationResponse response = new NotificationResponse();
        response.setId(1L);
        response.setUserId(1L);
        response.setType("ORDER_CONFIRMATION");
        response.setMessage("Your order has been confirmed");
        response.setCreatedAt(LocalDateTime.now());
        response.setStatus("UNREAD");

        when(notificationService.sendNotification(any(NotificationRequest.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.type").value("ORDER_CONFIRMATION"))
                .andExpect(jsonPath("$.message").value("Your order has been confirmed"))
                .andExpect(jsonPath("$.status").value("UNREAD"));
    }

    @Test
    @DisplayName("Should get notification by ID")
    void testGetNotificationById() throws Exception {
        // Arrange
        NotificationResponse response = new NotificationResponse();
        response.setId(1L);
        response.setUserId(1L);
        response.setType("ORDER_CONFIRMATION");
        response.setMessage("Your order has been confirmed");
        response.setCreatedAt(LocalDateTime.now());
        response.setStatus("UNREAD");

        when(notificationService.getNotificationById(1L)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.type").value("ORDER_CONFIRMATION"))
                .andExpect(jsonPath("$.message").value("Your order has been confirmed"))
                .andExpect(jsonPath("$.status").value("UNREAD"));
    }

    @Test
    @DisplayName("Should get user notifications")
    void testGetUserNotifications() throws Exception {
        // Arrange
        NotificationResponse notification1 = new NotificationResponse();
        notification1.setId(1L);
        notification1.setUserId(1L);
        notification1.setType("ORDER_CONFIRMATION");
        notification1.setMessage("Your order has been confirmed");
        notification1.setCreatedAt(LocalDateTime.now());
        notification1.setStatus("UNREAD");

        NotificationResponse notification2 = new NotificationResponse();
        notification2.setId(2L);
        notification2.setUserId(1L);
        notification2.setType("ORDER_SHIPPED");
        notification2.setMessage("Your order has been shipped");
        notification2.setCreatedAt(LocalDateTime.now());
        notification2.setStatus("UNREAD");

        List<NotificationResponse> notifications = Arrays.asList(notification1, notification2);

        when(notificationService.getUserNotifications(1L)).thenReturn(notifications);

        // Act & Assert
        mockMvc.perform(get("/notifications/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].type").value("ORDER_CONFIRMATION"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].type").value("ORDER_SHIPPED"));
    }

    @Test
    @DisplayName("Should validate notification request")
    void testSendNotification_ValidationError() throws Exception {
        // Arrange
        NotificationRequest request = new NotificationRequest();
        // Missing required fields

        // Act & Assert
        mockMvc.perform(post("/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}