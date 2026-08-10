package com.edugauge.service;

import com.edugauge.domain.Notification.Notification;
import com.edugauge.domain.Notification.NotificationType;
import com.edugauge.domain.user.User;
import com.edugauge.dto.NotificationResponse;
import com.edugauge.repositiry.NotificationRepository;
import com.edugauge.repositiry.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createFriendRequestNotification(
            Long receiverId,
            User sender
    ) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다")
                );

        if (!receiver.isFriendRequestNotificationEnabled()) {
            return;
        }

        Notification notification = new Notification(
                receiver,
                sender,
                NotificationType.FRIEND_REQUEST,
                sender.getNickname() + "님이 친구 요청을 보냈습니다."
        );

        notificationRepository.save(notification);
    }

    public void createWakeUpNotification(
            Long receiverId,
            User sender
    ) {
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다")
                );
        if (!receiver.isWakeUpNotificationEnabled()) {
            return;
        }
        if (hasSentWakeUpToday(sender.getId(), receiver.getId())) {
            throw new IllegalArgumentException("오늘 이미 깨우기 알림을 보냈습니다");
        }



        Notification notification = new Notification(
                receiver,
                sender,
                NotificationType.WAKE_UP,
                sender.getNickname() + "님이 깨우기 알림을 보냈습니다."
        );

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> getNotifications(Long userId) {
        return notificationRepository.findByReceiver_IdOrderByCreatedAtDesc(userId)
                .stream()
                .map(notification -> new NotificationResponse(
                        notification.getId(),
                        notification.getType(),
                        notification.getMessage(),
                        notification.isRead(),
                        notification.getCreatedAt()
                ))
                .toList();
    }

    public void deleteNotification(
            Long userId,
            Long notificationId
    ) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("알림을 찾을 수 없습니다")
                );

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 알림만 삭제할 수 있습니다");
        }

        notificationRepository.delete(notification);
    }
    public void readNotification(
            Long userId,
            Long notificationId
    ) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("알림을 찾을 수 없습니다")
                );

        if (!notification.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 알림만 읽음 처리할 수 있습니다");
        }

        notification.read();
    }
    public boolean hasSentWakeUpToday(
            Long senderId,
            Long receiverId
    ) {
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return notificationRepository.existsBySender_IdAndReceiver_IdAndTypeAndCreatedAtBetween(
                senderId,
                receiverId,
                NotificationType.WAKE_UP,
                start,
                end
        );
    }
}