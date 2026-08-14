package com.edugauge.service;

import com.edugauge.domain.DailyProgress;
import com.edugauge.domain.friendship.Friendship;
import com.edugauge.domain.friendship.FriendshipStatus;
import com.edugauge.domain.user.User;
import com.edugauge.dto.FriendRequestCreateRequest;
import com.edugauge.dto.FriendRequestResponse;
import com.edugauge.dto.FriendRequestUpdateRequest;
import com.edugauge.dto.FriendResponse;
import com.edugauge.repositiry.DailyProgressRepository;
import com.edugauge.repositiry.FriendshipRepository;
import com.edugauge.repositiry.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipService {
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final DailyProgressRepository dailyProgressRepository;
    private final NotificationService notificationService;
    private final StudyDateService studyDateService;

    public void sendFriendRequest(
            Long userId,
            FriendRequestCreateRequest request
    ) {
        if (userId.equals(request.getReceiverId())) {
            throw new IllegalArgumentException("본인에게 친구 요청을 보낼 수 없습니다");
        }

        User requester = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다")
                );

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() ->
                        new IllegalArgumentException("친구 요청을 받을 사용자를 찾을 수 없습니다")
                );

        boolean alreadyRequested =
                friendshipRepository.existsByRequester_IdAndReceiver_IdAndStatus(
                        userId,
                        request.getReceiverId(),
                        FriendshipStatus.PENDING
                );

        boolean alreadyReceived =
                friendshipRepository.existsByReceiver_IdAndRequester_IdAndStatus(
                        userId,
                        request.getReceiverId(),
                        FriendshipStatus.PENDING
                );

        boolean alreadyFriend1 =
                friendshipRepository.existsByRequester_IdAndReceiver_IdAndStatus(
                        userId,
                        request.getReceiverId(),
                        FriendshipStatus.ACCEPTED
                );

        boolean alreadyFriend2 =
                friendshipRepository.existsByReceiver_IdAndRequester_IdAndStatus(
                        userId,
                        request.getReceiverId(),
                        FriendshipStatus.ACCEPTED
                );

        if (alreadyRequested || alreadyReceived || alreadyFriend1 || alreadyFriend2) {
            throw new IllegalArgumentException("이미 친구 요청이 있거나 친구 상태입니다");
        }

        Friendship friendship = new Friendship(
                requester,
                receiver
        );

        friendshipRepository.save(friendship);

        notificationService.createFriendRequestNotification(
                receiver.getId(),
                requester
        );
    }

    public List<FriendRequestResponse> getReceivedFriendRequests(Long userId) {
        return friendshipRepository.findByReceiver_IdAndStatus(
                        userId,
                        FriendshipStatus.PENDING
                )
                .stream()
                .map(friendship -> new FriendRequestResponse(
                        friendship.getId(),
                        friendship.getRequester().getId(),
                        friendship.getRequester().getNickname()
                ))
                .toList();
    }

    public void updateFriendRequest(
            Long userId,
            Long friendshipId,
            FriendRequestUpdateRequest request
    ) {
        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() ->
                        new IllegalArgumentException("친구 요청을 찾을 수 없습니다")
                );

        if (!friendship.getReceiver().getId().equals(userId)) {
            throw new IllegalArgumentException("본인이 받은 친구 요청만 처리할 수 있습니다");
        }

        if (friendship.getStatus() != FriendshipStatus.PENDING) {
            throw new IllegalArgumentException("이미 처리된 친구 요청입니다");
        }

        if ("ACCEPT".equals(request.getAction())) {
            friendship.accept();
            return;
        }

        if ("REJECT".equals(request.getAction())) {
            friendship.reject();
            return;
        }

        throw new IllegalArgumentException("올바르지 않은 요청입니다");
    }

    public List<FriendResponse> getFriends(Long userId) {
        LocalDate today = getStudyDate();

        List<Friendship> friendships =
                friendshipRepository.findByRequester_IdAndStatusOrReceiver_IdAndStatus(
                        userId,
                        FriendshipStatus.ACCEPTED,
                        userId,
                        FriendshipStatus.ACCEPTED
                );

        return friendships.stream()
                .map(friendship -> {
                    User friend = friendship.getRequester().getId().equals(userId)
                            ? friendship.getReceiver()
                            : friendship.getRequester();

                    int progress = dailyProgressRepository
                            .findByUser_IdAndProgressDate(friend.getId(), today)
                            .map(DailyProgress::calculateGauge)
                            .orElse(0);
                    boolean wakeUpAvailable =
                            !notificationService.hasSentWakeUpToday(userId, friend.getId());

                    return new FriendResponse(
                            friend.getId(),
                            friend.getNickname(),
                            progress,
                            wakeUpAvailable
                    );



                })
                .toList();
    }

    public void wakeUpFriend(
            Long userId,
            Long friendId
    ) {
        User sender = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("사용자를 찾을 수 없습니다")
                );

        boolean isFriend1 =
                friendshipRepository.existsByRequester_IdAndReceiver_IdAndStatus(
                        userId,
                        friendId,
                        FriendshipStatus.ACCEPTED
                );

        boolean isFriend2 =
                friendshipRepository.existsByReceiver_IdAndRequester_IdAndStatus(
                        userId,
                        friendId,
                        FriendshipStatus.ACCEPTED
                );

        if (!isFriend1 && !isFriend2) {
            throw new IllegalArgumentException("친구에게만 깨우기 알림을 보낼 수 있습니다");
        }

        notificationService.createWakeUpNotification(
                friendId,
                sender
        );
    }

    private LocalDate getStudyDate() {
        return studyDateService.getCurrentStudyDate();
    }
}
