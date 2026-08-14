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
import java.time.LocalDateTime;
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
        LocalDateTime currentResetAt = studyDateService.getCurrentResetAt();

        cleanupInvalidRolloverRecord(userId, recordDate, currentResetAt);

        if (dailyTodoRecordRepository.existsByUser_IdAndRecordDate(
                userId,
                recordDate
        )) {
            return;
        }

        List<Todo> todos = todoRepository.findByUser_Id(userId);
        List<Todo> rolloverTodos = todos.stream()
                .filter(todo -> todo.getCreatedAt() == null
                        || todo.getCreatedAt().isBefore(currentResetAt))
                .toList();

        if (rolloverTodos.isEmpty()) {
            return;
        }

        saveDailyTodoRecords(
                userId,
                recordDate,
                rolloverTodos
        );
        resetDailyTodos(rolloverTodos);
    }

    public void cleanupInvalidPreviousRecord(Long userId) {
        if (studyDateService.isBeforeResetTime()) {
            return;
        }

        LocalDate recordDate = studyDateService.getCurrentStudyDate()
                .minusDays(1);
        LocalDateTime currentResetAt = studyDateService.getCurrentResetAt();

        cleanupInvalidRolloverRecord(userId, recordDate, currentResetAt);
    }

    private void cleanupInvalidRolloverRecord(
            Long userId,
            LocalDate recordDate,
            LocalDateTime currentResetAt
    ) {
        List<DailyTodoRecord> records =
                dailyTodoRecordRepository.findByUser_IdAndRecordDate(
                        userId,
                        recordDate
                );

        if (records.isEmpty()) {
            return;
        }

        boolean allRecordsCreatedAfterReset = records.stream()
                .allMatch(record -> todoRepository.findById(record.getTodoId())
                        .map(todo -> todo.getCreatedAt() != null
                                && !todo.getCreatedAt().isBefore(currentResetAt))
                        .orElse(false));

        if (!allRecordsCreatedAfterReset) {
            return;
        }

        dailyTodoRecordRepository.deleteByUser_IdAndRecordDate(
                userId,
                recordDate
        );
        dailyProgressRepository.deleteByUser_IdAndProgressDate(
                userId,
                recordDate
        );
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
        saveDailyTodoRecords(userId, recordDate, todos);
    }

    private void saveDailyTodoRecords(
            Long userId,
            LocalDate recordDate,
            List<Todo> todos
    ) {
        if (dailyTodoRecordRepository.existsByUser_IdAndRecordDate(
                userId,
                recordDate
        )) {
            return;
        }

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
        resetDailyTodos(todos);
    }

    private void resetDailyTodos(List<Todo> todos) {
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
