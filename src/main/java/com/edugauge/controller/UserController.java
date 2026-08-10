package com.edugauge.controller;

import com.edugauge.dto.MyPageResponse;
import com.edugauge.dto.MyPageUpdateRequest;
import com.edugauge.dto.UserSearchResponse;
import com.edugauge.dto.UserSignupRequest;
import com.edugauge.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;
    @PostMapping("/signup")
    public void signup(@RequestBody UserSignupRequest request){
        userService.join(request);

    }
    @GetMapping("/me")
    public MyPageResponse getMyPage(
            @AuthenticationPrincipal Long userId
    ){
        return userService.getMyPage(userId);

    }
    @PatchMapping("/me")
    public MyPageResponse updateMyPage(
            @AuthenticationPrincipal Long userId,
            @RequestBody MyPageUpdateRequest request
    ) {
        return userService.updateMyPage(userId, request);
    }
    @GetMapping("/search")
    public List<UserSearchResponse> searchUsers(
            @AuthenticationPrincipal Long userId,
            @RequestParam String keyword
    ) {
        return userService.searchUsers(userId, keyword);
    }
}
