package com.edugauge.controller;

import com.edugauge.dto.TimerResponse;
import com.edugauge.service.TimerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timers")
public class TimerController {
    private final TimerService timerService;
    @PostMapping

    public void startTimer(
            @AuthenticationPrincipal Long userId
    ){
        timerService.startTimer(userId);
    }
    @PatchMapping("/pause")
    public void pauseTimer(
            @AuthenticationPrincipal Long userId
    ){
        timerService.pauseTimer(userId);
    }

    @PatchMapping("/stop")
    public void stopTimer(
            @AuthenticationPrincipal Long userId
    ){
        timerService.stopTimer(userId);
    }

    @GetMapping
    public TimerResponse getTimer(
            @AuthenticationPrincipal Long userId
    ){
        return timerService.getTimer(userId);
    }

}
