package com.edugauge.controller;

import com.edugauge.dto.FriendRequestCreateRequest;
import com.edugauge.dto.FriendRequestResponse;
import com.edugauge.dto.FriendRequestUpdateRequest;
import com.edugauge.dto.FriendResponse;
import com.edugauge.service.FriendshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FriendshipController {
    private final FriendshipService friendshipService;

    @PostMapping("/api/friend-requests")
    public void sendFriendRequest(
            @AuthenticationPrincipal Long userId,
            @RequestBody FriendRequestCreateRequest request
    ) {
        friendshipService.sendFriendRequest(userId, request);
    }

    @GetMapping("/api/friend-requests")
    public List<FriendRequestResponse> getReceivedFriendRequests(
            @AuthenticationPrincipal Long userId
    ) {
        return friendshipService.getReceivedFriendRequests(userId);
    }

    @PatchMapping("/api/friend-requests/{friendshipId}")
    public void updateFriendRequest(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long friendshipId,
            @RequestBody FriendRequestUpdateRequest request
    ) {
        friendshipService.updateFriendRequest(userId, friendshipId, request);
    }

    @GetMapping("/api/friends")
    public List<FriendResponse> getFriends(
            @AuthenticationPrincipal Long userId
    ) {
        return friendshipService.getFriends(userId);
    }

    @PostMapping("/api/friends/{friendId}/wake-up")
    public void wakeUpFriend(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long friendId
    ) {
        friendshipService.wakeUpFriend(userId, friendId);
    }
}