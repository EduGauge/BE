package com.edugauge.controller;

import com.edugauge.dto.LoginRequest;
import com.edugauge.dto.LoginResponse;
import com.edugauge.dto.TokenReissueRequest;
import com.edugauge.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    @PostMapping("/reissue")
    public LoginResponse reissue(
            @RequestBody TokenReissueRequest request
    ) {
        return authService.reissue(request);
    }

    @PostMapping("/logout")
    public void logout(
            @AuthenticationPrincipal Long userId
    ) {
        authService.logout(userId);
    }
}