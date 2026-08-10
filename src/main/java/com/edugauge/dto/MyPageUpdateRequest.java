package com.edugauge.dto;

import com.edugauge.domain.user.ThemeMode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageUpdateRequest {
    private String nickname;
    private String profileImageUrl;
    private ThemeMode themeMode;
    private Boolean wakeUpNotificationEnabled;
    private Boolean friendRequestNotificationEnabled;
}
