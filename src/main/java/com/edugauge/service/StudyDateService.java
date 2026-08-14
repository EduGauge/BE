package com.edugauge.service;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
public class StudyDateService {
    private static final LocalTime RESET_TIME = LocalTime.of(6, 0);

    public LocalDate getCurrentStudyDate() {
        return getStudyDate(LocalDateTime.now());
    }

    public LocalDate getStudyDate(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();

        if (dateTime.toLocalTime().isBefore(RESET_TIME)) {
            return date.minusDays(1);
        }

        return date;
    }

    public LocalDate getRecordDateForDailyReset() {
        return LocalDate.now().minusDays(1);
    }
}
