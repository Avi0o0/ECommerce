package com.ecom.notificationservice.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecom.notificationservice.dto.NotificationRequest;
import com.ecom.notificationservice.dto.NotificationResponse;
import com.ecom.notificationservice.entity.Notification;
import com.ecom.notificationservice.exception.NotificationNotFoundException;
import com.ecom.notificationservice.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification testNotification;
    private NotificationRequest testRequest;

    @BeforeEach
    void setUp() {
        testNotification = new Notification("1L", "Test message", "TEST_TYPE");
        testNotification.setId(1L);
        testNotification.setStatus("SENT");
        testNotification.setCreatedAt(LocalDateTime.now());

        testRequest = new NotificationRequest();
        testRequest.setUserId("1L");
        testRequest.setMessage("Test message");
        testRequest.setType("TEST_TYPE");
    }

    @Test
    void shouldSendNotification() {
        // Arrange
        when(notificationRepository.save(any(Notification.class))).thenReturn(testNotification);

        // Act
        NotificationResponse response = notificationService.sendNotification(testRequest);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testNotification.getId());
        assertThat(response.getUserId()).isEqualTo(testNotification.getUserId());
        assertThat(response.getMessage()).isEqualTo(testNotification.getMessage());
        assertThat(response.getType()).isEqualTo(testNotification.getType());
        assertThat(response.getStatus()).isEqualTo(testNotification.getStatus());
        assertThat(response.getCreatedAt()).isEqualTo(testNotification.getCreatedAt());
    }

    @Test
    void shouldGetNotificationById() {
        // Arrange
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(testNotification));

        // Act
        NotificationResponse response = notificationService.getNotificationById(1L);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(testNotification.getId());
        assertThat(response.getUserId()).isEqualTo(testNotification.getUserId());
        assertThat(response.getMessage()).isEqualTo(testNotification.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenNotificationNotFound() {
        // Arrange
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> notificationService.getNotificationById(999L))
            .isInstanceOf(NotificationNotFoundException.class)
            .hasMessageContaining("Notification not found with ID: 999");
    }

    @Test
    void shouldGetUserNotifications() {
        // Arrange
        Notification notification1 = new Notification("1L", "First message", "TEST_TYPE");
        notification1.setId(1L);
        notification1.setStatus("SENT");
        notification1.setCreatedAt(LocalDateTime.now().minusHours(1));

        Notification notification2 = new Notification("1L", "Second message", "TEST_TYPE");
        notification2.setId(2L);
        notification2.setStatus("SENT");
        notification2.setCreatedAt(LocalDateTime.now());

        List<Notification> notifications = Arrays.asList(notification1, notification2);
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc("1L")).thenReturn(notifications);

        // Act
        List<NotificationResponse> responses = notificationService.getUserNotifications("1L");

        // Assert
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getMessage()).isEqualTo("First message");
        assertThat(responses.get(1).getMessage()).isEqualTo("Second message");
    }

    @Test
    void shouldReturnEmptyListWhenNoNotificationsFoundForUser() {
        // Arrange
        when(notificationRepository.findByUserIdOrderByCreatedAtDesc("999L")).thenReturn(List.of());

        // Act
        List<NotificationResponse> responses = notificationService.getUserNotifications("999L");

        // Assert
        assertThat(responses).isEmpty();
    }
}