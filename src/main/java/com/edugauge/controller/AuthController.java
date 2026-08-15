package com.edugauge.controller;

import com.edugauge.dto.LoginRequest;
import com.edugauge.dto.LoginResponse;
import com.edugauge.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public LoginResponse login(
            @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        LoginResponse token = authService.login(request);
        addAuthCookies(response, token);
        return token;
    }

    @PostMapping("/reissue")
    public LoginResponse reissue(
            @CookieValue("refreshToken") String refreshToken,
            HttpServletResponse response
    ) {
        LoginResponse token = authService.reissue(refreshToken);
        addAuthCookies(response, token);
        return token;
    }

    @PostMapping("/logout")
    public void logout(
            @AuthenticationPrincipal Long userId,
            HttpServletResponse response
    ) {
        authService.logout(userId);
        deleteAuthCookies(response);
    }

    @GetMapping("/google")
    public void googleLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
    }

    @GetMapping("/kakao")
    public void kakaoLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/kakao");
    }

    @GetMapping("/naver")
    public void naverLogin(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/naver");
    }

    private void addAuthCookies(HttpServletResponse response, LoginResponse token) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", token.getAccessToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", token.getRefreshToken())
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(60 * 60 * 24 * 14)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }

    private void deleteAuthCookies(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
    }
}