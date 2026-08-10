package com.edugauge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendResponse {
    private Long userId;
    private String nickname;
    private int progress;
    private boolean wakeUpAvailable;
}