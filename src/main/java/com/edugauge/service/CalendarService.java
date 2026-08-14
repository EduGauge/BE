package com.edugauge.service;

import com.edugauge.domain.CharacterFace;
import com.edugauge.domain.DailyProgress;
import com.edugauge.domain.ProofImage;
import com.edugauge.domain.StudyRecord;
import com.edugauge.domain.Todo;
import com.edugauge.domain.timer.Timer;
import com.edugauge.domain.timer.TimerStatus;
import com.edugauge.domain.user.DailyTodoRecord;
import com.edugauge.dto.CalendarDayResponse;
import com.edugauge.dto.CalendarDetailResponse;
import com.edugauge.dto.CalendarTodoResponse;
import com.edugauge.dto.ProofImageResponse;
import com.edugauge.repositiry.DailyProgressRepository;
import com.edugauge.repositiry.DailyTodoRecordRepository;
import com.edugauge.repositiry.StudyRecordRepository;
import com.edugauge.repositiry.TimerRepository;
import com.edugauge.repositiry.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.edugauge.repositiry.ProofImageRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final StudyRecordRepository studyRecordRepository;
    private final ProofImageRepository proofImageRepository;
    private final DailyProgressRepository dailyProgressRepository;
    private final DailyTodoRecordRepository dailyTodoRecordRepository;
    private final DailyTodoRecordService dailyTodoRecordService;
    private final TodoRepository todoRepository;
    private final TimerRepository timerRepository;
    private final StudyDateService studyDateService;

    public CalendarDetailResponse getCalendarDetail(Long userId, LocalDate date){
        dailyTodoRecordService.cleanupInvalidPreviousRecord(userId);

        Optional<StudyRecord> studyRecord =
                studyRecordRepository.findByUser_IdAndStudyDate(userId,date);

        long studySeconds = getStudySeconds(userId, date, studyRecord);
        Optional<ProofImage> proofImage =
                proofImageRepository.findByUser_IdAndProofDate(
                        userId,
                        date
                );


        ProofImageResponse proofImageResponse = proofImage
                .map(image -> new ProofImageResponse(
                        image.getId(),
                        image.getImageUrl(),
                        image.getCreatedAt()
                ))
                .orElse(null);

        Optional<DailyProgress> progress =
                dailyProgressRepository.findByUser_IdAndProgressDate(
                        userId,
                        date
                );
        int gauge = progress.map(DailyProgress::calculateGauge).orElse(0);
        CharacterFace characterFace = progress
                .map(DailyProgress::calculateCharacterFace)
                .orElse(CharacterFace.VERY_SAD);
        String message = createMessage(characterFace);
        List<CalendarTodoResponse> todos = getCalendarTodos(userId, date);
        int totalTodoCount = todos.size();

        int completedTodoCount = (int) todos.stream()
                .filter(CalendarTodoResponse::isCompleted)
                .count();
        return new CalendarDetailResponse(
                date,
                gauge,
                characterFace,
                message,
                studySeconds,
                proofImageResponse,
                totalTodoCount,
                completedTodoCount,
                todos
        );

    }

    private long getStudySeconds(
            Long userId,
            LocalDate date,
            Optional<StudyRecord> studyRecord
    ) {
        long savedSeconds = studyRecord.map(StudyRecord::getStudySeconds)
                .orElse(0L);

        if (!date.equals(studyDateService.getCurrentStudyDate())) {
            return savedSeconds;
        }

        return timerRepository.findByUser_Id(userId)
                .map(timer -> calculateCurrentStudySeconds(savedSeconds, timer))
                .orElse(savedSeconds);
    }

    private long calculateCurrentStudySeconds(
            long savedSeconds,
            Timer timer
    ) {
        if (timer.getTimerStatus() == TimerStatus.STOPPED) {
            return Math.max(savedSeconds, timer.getAccumulatedSeconds());
        }

        long activeSeconds = timer.getAccumulatedSeconds();

        if (timer.getTimerStatus() == TimerStatus.RUNNING) {
            LocalDateTime now = studyDateService.now();
            activeSeconds += Duration.between(
                    timer.getStartedAt(),
                    now
            ).getSeconds();
        }

        return savedSeconds + Math.max(activeSeconds, 0L);
    }

    private List<CalendarTodoResponse> getCalendarTodos(
            Long userId,
            LocalDate date
    ) {
        if (date.equals(studyDateService.getCurrentStudyDate())) {
            List<Todo> currentTodos = todoRepository.findByUser_Id(userId);

            return currentTodos.stream()
                    .map(todo -> new CalendarTodoResponse(
                            todo.getId(),
                            todo.getTitle(),
                            todo.getCategory().getName(),
                            todo.isCompleted()
                    ))
                    .toList();
        }

        List<DailyTodoRecord> todoRecords =
                dailyTodoRecordRepository.findByUser_IdAndRecordDate(
                        userId,
                        date
                );

        return todoRecords.stream()
                .map(record -> new CalendarTodoResponse(
                        record.getTodoId(),
                        record.getTitle(),
                        record.getCategoryName(),
                        record.isCompleted()
                ))
                .toList();
    }

    public List<CalendarDayResponse>getCalendarMonth(
            Long userId,
            int year,
            int month
    ) {
        dailyTodoRecordService.cleanupInvalidPreviousRecord(userId);

        LocalDate startDate = LocalDate.of(
                year,
                month,
                1
        );

        LocalDate endDate = startDate.withDayOfMonth(
                startDate.lengthOfMonth()
        );
        List<DailyProgress> progresses =
                dailyProgressRepository.findByUser_IdAndProgressDateBetween(
                        userId,
                        startDate,
                        endDate
                );
        return progresses.stream()
                .map(progress -> new CalendarDayResponse(
                        progress.getProgressDate(),
                        progress.calculateGauge(),
                        progress.calculateCharacterFace()

                ))
                .toList();
    }
    private String createMessage(CharacterFace characterFace) {
        return switch (characterFace) {
            case VERY_SAD -> "아직 시작 전이에요. 하나만 해도 충분해요!";
            case SAD -> "조금씩 올라가고 있어요. 다음 Todo 하나만 더 해봐요.";
            case NORMAL -> "좋아요! 오늘의 흐름을 잡았어요.";
            case HAPPY -> "잘하고 있어요! 조금만 더 하면 목표 달성이에요.";
            case VERY_HAPPY -> "완벽해요! 오늘의 게이지를 꽉 채웠어요.";
        };
    }
}
