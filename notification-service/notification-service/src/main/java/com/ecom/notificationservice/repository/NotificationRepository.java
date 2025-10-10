package com.ecom.notificationservice.repository;

import com.ecom.notificationservice.entity.Notification;
import com.ecom.notificationservice.entity.NotificationStatus;
import com.ecom.notificationservice.entity.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find notifications by user ID
     */
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * Find notifications by user ID with pagination
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * Find notifications by type
     */
    List<Notification> findByTypeOrderByCreatedAtDesc(NotificationType type);
    
    /**
     * Find notifications by status
     */
    List<Notification> findByStatusOrderByCreatedAtDesc(NotificationStatus status);
    
    /**
     * Find notifications by user ID and type
     */
    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NotificationType type);
    
    /**
     * Find notifications by user ID and status
     */
    List<Notification> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, NotificationStatus status);
    
    /**
     * Find failed notifications that can be retried
     */
    @Query("SELECT n FROM Notification n WHERE n.status = 'FAILED' AND n.retryCount < n.maxRetries ORDER BY n.createdAt ASC")
    List<Notification> findFailedNotificationsForRetry();
    
    /**
     * Find notifications created between two dates
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                                   @Param("endDate") LocalDateTime endDate);
    
    /**
     * Find notifications by user ID created between two dates
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.createdAt BETWEEN :startDate AND :endDate ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByUserBetweenDates(@Param("userId") Long userId,
                                                         @Param("startDate") LocalDateTime startDate, 
                                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count notifications by user ID
     */
    long countByUserId(Long userId);
    
    /**
     * Count notifications by type
     */
    long countByType(NotificationType type);
    
    /**
     * Count notifications by status
     */
    long countByStatus(NotificationStatus status);
    
    /**
     * Count notifications by user ID and type
     */
    long countByUserIdAndType(Long userId, NotificationType type);
    
    /**
     * Count notifications by user ID and status
     */
    long countByUserIdAndStatus(Long userId, NotificationStatus status);
    
    /**
     * Find notifications with retry count greater than specified value
     */
    @Query("SELECT n FROM Notification n WHERE n.retryCount > :retryCount ORDER BY n.createdAt DESC")
    List<Notification> findByRetryCountGreaterThan(@Param("retryCount") Integer retryCount);
    
    /**
     * Find notifications older than specified time
     */
    @Query("SELECT n FROM Notification n WHERE n.createdAt < :cutoffTime ORDER BY n.createdAt ASC")
    List<Notification> findNotificationsOlderThan(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Find notifications by user ID with message containing text
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId AND n.message LIKE %:text% ORDER BY n.createdAt DESC")
    List<Notification> findByUserIdAndMessageContaining(@Param("userId") Long userId, @Param("text") String text);
}
