package com.edugauge.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public Long Test(
            @AuthenticationPrincipal Long userId
    ){
        return userId;
    }
}
