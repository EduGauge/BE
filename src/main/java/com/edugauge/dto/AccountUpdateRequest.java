package com.edugauge.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AccountUpdateRequest {
    private String loginId;
    private String currentPassword;
    private String newPassword;
}
