package com.edugauge.service;

import com.edugauge.domain.user.ThemeMode;
import com.edugauge.domain.user.User;
import com.edugauge.dto.MyPageResponse;
import com.edugauge.dto.MyPageUpdateRequest;
import com.edugauge.dto.UserSignupRequest;
import com.edugauge.repositiry.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.edugauge.domain.friendship.FriendshipStatus;
import com.edugauge.dto.FriendStatus;
import com.edugauge.dto.UserSearchResponse;
import com.edugauge.dto.AccountUpdateRequest;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FriendshipRepository friendshipRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TodoRepository todoRepository;
    private final CategoryRepository categoryRepository;
    private final TimerRepository timerRepository;
    private final StudyRecordRepository studyRecordRepository;
    private final DailyProgressRepository dailyProgressRepository;
    private final DailyTodoRecordRepository dailyTodoRecordRepository;
    private final ProofImageRepository proofImageRepository;
    private final NotificationRepository notificationRepository;

    public void join(UserSignupRequest request){
        if(userRepository.existsByLoginId((request.getLoginId()))){
            throw new IllegalArgumentException(("이미 사용 중인 아이디입니다"));
        }

        if(userRepository.existsByNickname(request.getNickname())){
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다");
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = new User(
                request.getLoginId(),
                encodedPassword,
                request.getNickname()
        );
        userRepository.save(user);

    }
    public MyPageResponse getMyPage(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다")
                );
        return createMyPageResponse(user);

    }
    public MyPageResponse updateMyPage(
            Long userId,
            MyPageUpdateRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다")
                );
        if (request.getNickname() != null
                && !request.getNickname().equals(user.getNickname())
                && userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다");
        }

        user.updateMyPage(
                request.getNickname(),
                request.getProfileImageUrl(),
                request.getThemeMode(),
                request.getWakeUpNotificationEnabled(),
                request.getFriendRequestNotificationEnabled()
        );
        return createMyPageResponse(user);

    }
    public List<UserSearchResponse> searchUsers(
            Long userId,
            String keyword
    ) {
        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("검색어를 입력해주세요");
        }

        List<User> users = userRepository.findByLoginIdContainingOrNicknameContaining(
                keyword,
                keyword
        );

        return users.stream()
                .filter(user -> !user.getId().equals(userId))
                .map(user -> new UserSearchResponse(
                        user.getId(),
                        user.getLoginId(),
                        user.getNickname(),
                        getFriendStatus(userId, user.getId())
                ))
                .toList();
    }
    private FriendStatus getFriendStatus(
            Long userId,
            Long targetUserId
    ) {
        boolean friend1 =
                friendshipRepository.existsByRequester_IdAndReceiver_IdAndStatus(
                        userId,
                        targetUserId,
                        FriendshipStatus.ACCEPTED
                );

        boolean friend2 =
                friendshipRepository.existsByReceiver_IdAndRequester_IdAndStatus(
                        userId,
                        targetUserId,
                        FriendshipStatus.ACCEPTED
                );

        if (friend1 || friend2) {
            return FriendStatus.FRIEND;
        }

        boolean sent =
                friendshipRepository.existsByRequester_IdAndReceiver_IdAndStatus(
                        userId,
                        targetUserId,
                        FriendshipStatus.PENDING
                );

        if (sent) {
            return FriendStatus.REQUEST_SENT;
        }

        boolean received =
                friendshipRepository.existsByReceiver_IdAndRequester_IdAndStatus(
                        userId,
                        targetUserId,
                        FriendshipStatus.PENDING
                );

        if (received) {
            return FriendStatus.REQUEST_RECEIVED;
        }

        return FriendStatus.NONE;
    }
    private ThemeMode resolveThemeMode(ThemeMode themeMode) {
        if (themeMode != ThemeMode.AUTO) {
            return themeMode;
        }
        int hour = LocalTime.now().getHour();

        if (hour >= 6 && hour < 18) {
            return ThemeMode.LIGHT;
        }
        return ThemeMode.DARK;
    }
    private MyPageResponse createMyPageResponse(User user) {
        return new MyPageResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getTotalStudyTime(),
                user.getThemeMode(),
                resolveThemeMode(user.getThemeMode()),
                user.isWakeUpNotificationEnabled(),
                user.isFriendRequestNotificationEnabled(),
                user.getLevel(),
                user.getExperience(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
    public MyPageResponse updateAccount(Long userId, AccountUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        if (request.getLoginId() != null
                && !request.getLoginId().isBlank()
                && !request.getLoginId().equals(user.getLoginId())) {

            if (userRepository.existsByLoginId(request.getLoginId())) {
                throw new IllegalArgumentException("이미 사용 중인 아이디입니다");
            }

            user.updateLoginId(request.getLoginId());
        }

        if (request.getNewPassword() != null
                && !request.getNewPassword().isBlank()) {

            if (request.getCurrentPassword() == null
                    || !passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다");
            }

            user.updatePassword(passwordEncoder.encode(request.getNewPassword()));
        }

        return createMyPageResponse(user);
    }
    public void deleteMe(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다")
                );

        refreshTokenRepository.deleteByUser_Id(userId);
        notificationRepository.deleteByReceiver_IdOrSender_Id(userId, userId);
        friendshipRepository.deleteByRequester_IdOrReceiver_Id(userId, userId);
        proofImageRepository.deleteByUser_Id(userId);
        dailyTodoRecordRepository.deleteByUser_Id(userId);
        dailyProgressRepository.deleteByUser_Id(userId);
        studyRecordRepository.deleteByUser_Id(userId);
        timerRepository.deleteByUser_Id(userId);
        todoRepository.deleteByUser_Id(userId);
        categoryRepository.deleteByUser_Id(userId);

        userRepository.delete(user);
    }

}
