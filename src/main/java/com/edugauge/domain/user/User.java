package com.edugauge.domain.user;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login_id", nullable = false, unique = true, length = 100)
    private String loginId;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "nickname", nullable = false, unique = true, length = 10)
    private String nickname;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "total_study_time", nullable = false)
    private long totalStudyTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "theme_mode", nullable = false)
    private ThemeMode themeMode;

    @Column(name = "wake_up_notification_enabled", nullable = false)
    private boolean wakeUpNotificationEnabled;

    @Column(name = "friend_request_notification_enabled", nullable = false)
    private boolean friendRequestNotificationEnabled;

    @Column(name = "level", nullable = false)
    private int level;

    @Column(name = "experience", nullable = false)
    private int experience;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider", nullable = false, length = 20)
    private SocialProvider socialProvider;

    @Column(name = "social_id", length = 100)
    private String socialId;


    public User(String loginId, String password, String nickname) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = null;
        this.totalStudyTime = 0L;
        this.themeMode = ThemeMode.DARK;
        this.wakeUpNotificationEnabled = true;
        this.friendRequestNotificationEnabled = true;
        this.level = 1;
        this.experience = 0;
        this.socialProvider = SocialProvider.EMAIL;
        this.socialId = null;
    }

    public void updateMyPage(
            String nickname,
            String profileImageUrl,
            ThemeMode themeMode,
            Boolean wakeUpNotificationEnabled,
            Boolean friendRequestNotificationEnabled
    ) {
        if (nickname != null) {
            this.nickname = nickname;
        }

        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }

        if (themeMode != null) {
            this.themeMode = themeMode;
        }

        if (wakeUpNotificationEnabled != null) {
            this.wakeUpNotificationEnabled = wakeUpNotificationEnabled;
        }

        if (friendRequestNotificationEnabled != null) {
            this.friendRequestNotificationEnabled = friendRequestNotificationEnabled;
        }
    }
    public boolean addExperience(int amount) {
        if (this.level < 1) {
            this.level = 1;
        }
        this.experience += amount;

        boolean levelUp = false;

        while (this.experience >= 100) {
            this.experience -= 100;
            this.level++;
            levelUp = true;
        }

        return levelUp;
    }
    public User(
            String loginId,
            String password,
            String nickname,
            String profileImageUrl,
            SocialProvider socialProvider,
            String socialId
    ) {
        this.loginId = loginId;
        this.password = password;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.socialProvider = socialProvider;
        this.socialId = socialId;
        this.totalStudyTime = 0L;
        this.themeMode = ThemeMode.DARK;
        this.wakeUpNotificationEnabled = true;
        this.friendRequestNotificationEnabled = true;
        this.level = 1;
        this.experience = 0;
    }

    public void updateLoginId(String loginId) {
        this.loginId = loginId;
    }

    public void updatePassword(String password) {
        this.password = password;
    }


}
