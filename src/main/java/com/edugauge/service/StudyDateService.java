package com.edugauge.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class StudyDateService {
    private final LocalTime resetTime;

    public StudyDateService(
            @Value("${edugauge.daily-reset-time:06:00}") String resetTime
    ) {
        this.resetTime = LocalTime.parse(resetTime);
    }

    public LocalDate getCurrentStudyDate() {
        return getStudyDate(LocalDateTime.now());
    }

    public boolean isBeforeResetTime() {
        return LocalTime.now().isBefore(resetTime);
    }

    public LocalDate getStudyDate(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();

        if (dateTime.toLocalTime().isBefore(resetTime)) {
            return date.minusDays(1);
        }

        return date;
    }

    public LocalDate getRecordDateForDailyReset() {
        return getCurrentStudyDate().minusDays(1);
    }

    public LocalDateTime getCurrentResetAt() {
        return getCurrentStudyDate().atTime(resetTime);
    }

    public LocalDateTime getNextResetAt(LocalDateTime now) {
        LocalDateTime todayReset = now.toLocalDate().atTime(resetTime);

        if (now.isBefore(todayReset)) {
            return todayReset;
        }

        return todayReset.plusDays(1);
    }
}
