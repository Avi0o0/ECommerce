package com.ecom.notificationservice.repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import com.ecom.notificationservice.entity.Notification;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class NotificationRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private NotificationRepository notificationRepository;

    @Test
    void shouldSaveNotification() {
        // Arrange
        Notification notification = new Notification(1L, "Test message", "TEST_TYPE");
        notification.setStatus("SENT");

        // Act
        Notification savedNotification = notificationRepository.save(notification);

        // Assert
        assertThat(savedNotification.getId()).isNotNull();
        assertThat(savedNotification.getUserId()).isEqualTo(1L);
        assertThat(savedNotification.getMessage()).isEqualTo("Test message");
        assertThat(savedNotification.getType()).isEqualTo("TEST_TYPE");
        assertThat(savedNotification.getStatus()).isEqualTo("SENT");
        assertThat(savedNotification.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindNotificationsByUserIdOrderedByCreatedAt() {
        // Arrange
        Notification notification1 = new Notification(1L, "First message", "TEST_TYPE");
        notification1.setStatus("SENT");
        notification1.setCreatedAt(LocalDateTime.now().minusHours(1));

        Notification notification2 = new Notification(1L, "Second message", "TEST_TYPE");
        notification2.setStatus("SENT");
        notification2.setCreatedAt(LocalDateTime.now());

        Notification notification3 = new Notification(2L, "Other user message", "TEST_TYPE");
        notification3.setStatus("SENT");
        notification3.setCreatedAt(LocalDateTime.now());

        entityManager.persist(notification1);
        entityManager.persist(notification2);
        entityManager.persist(notification3);
        entityManager.flush();

        // Act
        List<Notification> userNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(1L);

        // Assert
        assertThat(userNotifications).hasSize(2);
        assertThat(userNotifications.get(0).getMessage()).isEqualTo("Second message");
        assertThat(userNotifications.get(1).getMessage()).isEqualTo("First message");
    }

    @Test
    void shouldReturnEmptyListWhenNoNotificationsFound() {
        // Act
        List<Notification> userNotifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(999L);

        // Assert
        assertThat(userNotifications).isEmpty();
    }
}