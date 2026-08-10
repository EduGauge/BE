package com.edugauge.controller;

import com.edugauge.dto.CalendarDayResponse;
import com.edugauge.dto.CalendarDetailResponse;
import com.edugauge.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/calendar")
public class CalendarController {
    private final CalendarService calendarService;
    @GetMapping("/{date}")
    public CalendarDetailResponse getCalenderDetail(
            @AuthenticationPrincipal Long userId,
            @PathVariable LocalDate date
            ){
        return  calendarService.getCalendarDetail(userId, date);
    }
    @GetMapping
    public List<CalendarDayResponse> getCalendarMonth(
            @AuthenticationPrincipal Long userId,
            @RequestParam int year,
            @RequestParam int month
    ) {
        return calendarService.getCalendarMonth(userId, year, month);
    }
}
