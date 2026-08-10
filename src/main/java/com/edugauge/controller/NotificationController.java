package com.edugauge.controller;

import com.edugauge.dto.NotificationResponse;
import com.edugauge.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public List<NotificationResponse> getNotifications(
            @AuthenticationPrincipal Long userId
    ) {
        return notificationService.getNotifications(userId);
    }

    @DeleteMapping("/{notificationId}")
    public void deleteNotification(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long notificationId
    ) {
        notificationService.deleteNotification(userId, notificationId);
    }
    @PatchMapping("/{notificationId}/read")
    public void readNotification(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long notificationId
    ) {
        notificationService.readNotification(userId, notificationId);
    }
}