package com.edugauge.controller;

import com.edugauge.dto.*;
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
    @PatchMapping("/me/account")
    public MyPageResponse updateAccount(
            @AuthenticationPrincipal Long userId,
            @RequestBody AccountUpdateRequest request
    ) {
        return userService.updateAccount(userId, request);
    }

    @DeleteMapping("/me")
    public void deleteMe(
            @AuthenticationPrincipal Long userId
    ) {
        userService.deleteMe(userId);
    }
    @GetMapping("/search")
    public List<UserSearchResponse> searchUsers(
            @AuthenticationPrincipal Long userId,
            @RequestParam String keyword
    ) {
        return userService.searchUsers(userId, keyword);
    }
}
