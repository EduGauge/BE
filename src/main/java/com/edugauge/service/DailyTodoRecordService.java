package com.edugauge.service;

import com.edugauge.domain.Todo;
import com.edugauge.domain.user.DailyTodoRecord;
import com.edugauge.repositiry.DailyTodoRecordRepository;
import com.edugauge.repositiry.TodoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DailyTodoRecordService {
    private final TodoRepository todoRepository;
    private final DailyTodoRecordRepository dailyTodoRecordRepository;
    public void saveDailyTodoRecords(
            Long userId,
            LocalDate recordDate
    ) {
        if (dailyTodoRecordRepository.existsByUser_IdAndRecordDate(
                userId,
                recordDate
        )) {
            return;
        }

        List<Todo> todos = todoRepository.findByUser_Id(userId);

        for (Todo todo : todos) {

            DailyTodoRecord record = new DailyTodoRecord(
                    todo.getUser(),
                    recordDate,
                    todo
            );

            dailyTodoRecordRepository.save(record);
        }
    }

}
