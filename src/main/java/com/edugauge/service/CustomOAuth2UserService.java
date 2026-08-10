package com.edugauge.service;

import com.edugauge.domain.user.SocialProvider;
import com.edugauge.domain.user.User;
import com.edugauge.jwt.CustomOAuth2User;
import com.edugauge.repositiry.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();

        log.info("OAuth login provider = {}", registrationId);
        log.info("OAuth attributes = {}", oAuth2User.getAttributes());

        OAuthUserInfo userInfo = extractUserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        User user = userRepository
                .findBySocialProviderAndSocialId(
                        userInfo.socialProvider(),
                        userInfo.socialId()
                )
                .orElseGet(() -> createSocialUser(userInfo));

        log.info("OAuth user loaded. userId={}, provider={}, socialId={}",
                user.getId(),
                userInfo.socialProvider(),
                userInfo.socialId()
        );

        return new CustomOAuth2User(
                user.getId(),
                oAuth2User.getAttributes()
        );
    }

    private User createSocialUser(OAuthUserInfo userInfo) {
        String loginId = userInfo.socialProvider().name().toLowerCase()
                + "_"
                + userInfo.socialId();

        String password = UUID.randomUUID().toString();
        String nickname = createUniqueNickname(userInfo.nickname(), userInfo.socialId());

        User user = new User(
                loginId,
                password,
                nickname,
                userInfo.profileImageUrl(),
                userInfo.socialProvider(),
                userInfo.socialId()
        );

        User savedUser = userRepository.save(user);

        log.info("New OAuth user saved. userId={}, loginId={}, nickname={}",
                savedUser.getId(),
                savedUser.getLoginId(),
                savedUser.getNickname()
        );

        return savedUser;
    }

    private String createUniqueNickname(String nickname, String socialId) {
        String baseNickname = createNickname(nickname);

        if (!userRepository.existsByNickname(baseNickname)) {
            return baseNickname;
        }

        String suffix = socialId == null ? UUID.randomUUID().toString() : socialId;
        suffix = suffix.replaceAll("[^a-zA-Z0-9]", "");

        if (suffix.length() > 4) {
            suffix = suffix.substring(suffix.length() - 4);
        }

        String uniqueNickname = baseNickname;

        int maxBaseLength = Math.max(1, 10 - suffix.length());
        uniqueNickname = baseNickname.substring(0, Math.min(baseNickname.length(), maxBaseLength)) + suffix;

        while (userRepository.existsByNickname(uniqueNickname)) {
            String randomSuffix = UUID.randomUUID().toString().substring(0, 4);
            uniqueNickname = baseNickname.substring(0, Math.min(baseNickname.length(), 6)) + randomSuffix;
        }

        return uniqueNickname;
    }

    private String createNickname(String nickname) {
        if (nickname == null || nickname.isBlank() || "null".equals(nickname)) {
            return "소셜유저";
        }

        String trimmed = nickname.trim();

        if (trimmed.length() > 10) {
            return trimmed.substring(0, 10);
        }

        return trimmed;
    }

    private OAuthUserInfo extractUserInfo(
            String registrationId,
            Map<String, Object> attributes
    ) {
        if ("google".equals(registrationId)) {
            return extractGoogle(attributes);
        }

        if ("kakao".equals(registrationId)) {
            return extractKakao(attributes);
        }

        if ("naver".equals(registrationId)) {
            return extractNaver(attributes);
        }

        throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다");
    }

    private OAuthUserInfo extractGoogle(Map<String, Object> attributes) {
        return new OAuthUserInfo(
                SocialProvider.GOOGLE,
                String.valueOf(attributes.get("sub")),
                String.valueOf(attributes.get("email")),
                String.valueOf(attributes.get("name")),
                String.valueOf(attributes.get("picture"))
        );
    }

    @SuppressWarnings("unchecked")
    private OAuthUserInfo extractKakao(Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount =
                (Map<String, Object>) attributes.get("kakao_account");

        Map<String, Object> profile =
                (Map<String, Object>) kakaoAccount.get("profile");

        return new OAuthUserInfo(
                SocialProvider.KAKAO,
                String.valueOf(attributes.get("id")),
                kakaoAccount == null ? null : String.valueOf(kakaoAccount.get("email")),
                profile == null ? null : String.valueOf(profile.get("nickname")),
                profile == null ? null : String.valueOf(profile.get("profile_image_url"))
        );
    }

    @SuppressWarnings("unchecked")
    private OAuthUserInfo extractNaver(Map<String, Object> attributes) {
        Map<String, Object> response =
                (Map<String, Object>) attributes.get("response");

        return new OAuthUserInfo(
                SocialProvider.NAVER,
                String.valueOf(response.get("id")),
                String.valueOf(response.get("email")),
                String.valueOf(response.get("name")),
                String.valueOf(response.get("profile_image"))
        );
    }

    private record OAuthUserInfo(
            SocialProvider socialProvider,
            String socialId,
            String email,
            String nickname,
            String profileImageUrl
    ) {
    }
}