package com.edugauge.service;

import com.edugauge.domain.user.User;
import com.edugauge.repositiry.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyTodoRecordScheduler {
    private final UserRepository userRepository;
    private final DailyTodoRecordService dailyTodoRecordService;
    private final StudyDateService studyDateService;

    @Scheduled(cron = "${edugauge.daily-reset-cron:0 0 6 * * *}", zone = "Asia/Seoul")
    public void saveDailyTodoRecordsAtSix() {
        LocalDate recordDate = studyDateService.getRecordDateForDailyReset();

        List<User> users = userRepository.findAll();

        for (User user : users) {
            dailyTodoRecordService.saveDailyTodoRecords(
                    user.getId(),
                    recordDate
            );
            dailyTodoRecordService.resetDailyTodos(user.getId());
        }
    }
}
