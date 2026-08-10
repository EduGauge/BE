package com.edugauge.dto;

import com.edugauge.domain.user.ThemeMode;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MyPageResponse {
    private Long userId;
    private String loginId;
    private String nickname;
    private String profileImageUrl;
    private long totalStudyTime;
    private ThemeMode themeMode;
    private ThemeMode effectiveThemeMode;
    private boolean wakeUpNotificationEnabled;
    private boolean friendRequestNotificationEnabled;
    private int level;
    private int experience;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}