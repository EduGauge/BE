package com.edugauge.dto;

import com.edugauge.domain.Notification.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class NotificationResponse {
    private Long notificationId;
    private NotificationType type;
    private String message;
    private boolean read;
    private LocalDateTime createdAt;
}
