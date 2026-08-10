package com.edugauge.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserSignupRequest {
    private String loginId;
    private String password;
    private String nickname;
}
