package com.edugauge.service;

import com.edugauge.domain.user.User;
import com.edugauge.dto.MyPageResponse;
import com.edugauge.dto.MyPageUpdateRequest;
import com.edugauge.dto.UserSignupRequest;
import com.edugauge.repositiry.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.edugauge.domain.friendship.FriendshipStatus;
import com.edugauge.dto.FriendStatus;
import com.edugauge.dto.UserSearchResponse;
import com.edugauge.repositiry.FriendshipRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final FriendshipRepository friendshipRepository;

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
        return new MyPageResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getTotalStudyTime(),
                user.getThemeMode(),
                user.isWakeUpNotificationEnabled(),
                user.isFriendRequestNotificationEnabled(),
                user.getLevel(),
                user.getExperience(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

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
        return new MyPageResponse(
                user.getId(),
                user.getLoginId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getTotalStudyTime(),
                user.getThemeMode(),
                user.isWakeUpNotificationEnabled(),
                user.isFriendRequestNotificationEnabled(),
                user.getLevel(),
                user.getExperience(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );

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

}
