package com.edugauge.repositiry;


import com.edugauge.domain.Notification.Notification;
import com.edugauge.domain.Notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByReceiver_IdOrderByCreatedAtDesc(Long receiverId);
    boolean existsBySender_IdAndReceiver_IdAndTypeAndCreatedAtBetween(
            Long senderId,
            Long receiverId,
            NotificationType type,
            LocalDateTime start,
            LocalDateTime end
    );
    void deleteByReceiver_IdOrSender_Id(Long receiverId, Long senderId);
}
