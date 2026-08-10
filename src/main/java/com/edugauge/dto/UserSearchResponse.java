package com.edugauge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserSearchResponse {
    private Long userId;
    private String loginId;
    private String nickname;
    private FriendStatus friendStatus;
}