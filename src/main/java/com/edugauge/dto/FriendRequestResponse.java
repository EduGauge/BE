package com.edugauge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FriendRequestResponse {
    private Long friendshipId;
    private Long requesterId;
    private String nickname;
}