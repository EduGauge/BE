package com.edugauge.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Service
public class StudyDateService {
    private final LocalTime resetTime;
    private final ZoneId zoneId;

    public StudyDateService(
            @Value("${edugauge.daily-reset-time:06:00}") String resetTime,
            @Value("${edugauge.time-zone:Asia/Seoul}") String timeZone
    ) {
        this.resetTime = LocalTime.parse(resetTime);
        this.zoneId = ZoneId.of(timeZone);
    }

    public LocalDate getCurrentStudyDate() {
        return getStudyDate(now());
    }

    public boolean isBeforeResetTime() {
        return now().toLocalTime().isBefore(resetTime);
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

    public LocalDateTime now() {
        return LocalDateTime.now(zoneId);
    }

    public LocalDateTime getNextResetAt(LocalDateTime now) {
        LocalDateTime todayReset = now.toLocalDate().atTime(resetTime);

        if (now.isBefore(todayReset)) {
            return todayReset;
        }

        return todayReset.plusDays(1);
    }
}
