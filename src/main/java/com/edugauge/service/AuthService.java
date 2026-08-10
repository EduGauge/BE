package com.edugauge.service;

import com.edugauge.domain.user.RefreshToken;
import com.edugauge.domain.user.User;
import com.edugauge.dto.LoginRequest;
import com.edugauge.dto.LoginResponse;
import com.edugauge.dto.TokenReissueRequest;
import com.edugauge.jwt.JwtProvider;
import com.edugauge.repositiry.RefreshTokenRepository;
import com.edugauge.repositiry.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByLoginId(request.getLoginId())
                .orElseThrow(() ->
                        new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다")
                );

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        refreshTokenRepository.findByUser_Id(user.getId())
                .ifPresentOrElse(
                        token -> token.updateToken(refreshToken),
                        () -> refreshTokenRepository.save(
                                new RefreshToken(user, refreshToken)
                        )
                );

        return new LoginResponse(
                accessToken,
                refreshToken
        );

    }
    public LoginResponse reissue(TokenReissueRequest request) {
        String refreshToken = request.getRefreshToken();

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token이 유효하지 않습니다");
        }

        RefreshToken savedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() ->
                        new IllegalArgumentException("Refresh Token이 존재하지 않습니다")
                );

        User user = savedToken.getUser();

        String newAccessToken = jwtProvider.generateAccessToken(user.getId());
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId());

        savedToken.updateToken(newRefreshToken);

        return new LoginResponse(
                newAccessToken,
                newRefreshToken
        );
    }
    public void logout(Long userId) {
        refreshTokenRepository.deleteByUser_Id(userId);
    }
}
