package com.edugauge.service;

import com.edugauge.domain.DailyProgress;
import com.edugauge.domain.Todo;
import com.edugauge.domain.user.DailyTodoRecord;
import com.edugauge.repositiry.DailyProgressRepository;
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
    private final DailyProgressRepository dailyProgressRepository;
    private final StudyDateService studyDateService;

    public void rolloverIfNeeded(Long userId) {
        if (studyDateService.isBeforeResetTime()) {
            return;
        }

        LocalDate recordDate = studyDateService.getCurrentStudyDate()
                .minusDays(1);

        if (dailyTodoRecordRepository.existsByUser_IdAndRecordDate(
                userId,
                recordDate
        )) {
            return;
        }

        saveDailyTodoRecords(
                userId,
                recordDate
        );
        resetDailyTodos(userId);
    }

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
        int completedTodoCount = 0;

        for (Todo todo : todos) {
            if (todo.isCompleted()) {
                completedTodoCount++;
            }

            DailyTodoRecord record = new DailyTodoRecord(
                    todo.getUser(),
                    recordDate,
                    todo
            );

            dailyTodoRecordRepository.save(record);
        }

        if (!todos.isEmpty()) {
            createDailyProgressIfMissing(
                    todos,
                    recordDate,
                    completedTodoCount
            );
        }
    }

    public void resetDailyTodos(Long userId) {
        List<Todo> todos = todoRepository.findByUser_Id(userId);

        for (Todo todo : todos) {
            todo.resetCompletion();
        }
    }

    private void createDailyProgressIfMissing(
            List<Todo> todos,
            LocalDate recordDate,
            int completedTodoCount
    ) {
        Todo firstTodo = todos.get(0);

        if (dailyProgressRepository.findByUser_IdAndProgressDate(
                firstTodo.getUser().getId(),
                recordDate
        ).isPresent()) {
            return;
        }

        DailyProgress dailyProgress = new DailyProgress(
                firstTodo.getUser(),
                recordDate,
                todos.size()
        );

        for (int i = 0; i < completedTodoCount; i++) {
            dailyProgress.completeTodo();
        }

        dailyProgressRepository.save(dailyProgress);
    }

}
