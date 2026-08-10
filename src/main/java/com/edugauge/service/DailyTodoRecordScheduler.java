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

    @Scheduled(cron = "0 0 6 * * *", zone = "Asia/Seoul")
    public void saveDailyTodoRecordsAtSix() {
        LocalDate recordDate = LocalDate.now().minusDays(1);

        List<User> users = userRepository.findAll();

        for (User user : users) {
            dailyTodoRecordService.saveDailyTodoRecords(
                    user.getId(),
                    recordDate
            );
        }
    }
}