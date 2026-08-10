package com.edugauge.service;

import com.edugauge.domain.CharacterFace;
import com.edugauge.domain.DailyProgress;
import com.edugauge.domain.ProofImage;
import com.edugauge.domain.StudyRecord;
import com.edugauge.domain.user.DailyTodoRecord;
import com.edugauge.dto.CalendarDayResponse;
import com.edugauge.dto.CalendarDetailResponse;
import com.edugauge.dto.CalendarTodoResponse;
import com.edugauge.dto.ProofImageResponse;
import com.edugauge.repositiry.DailyProgressRepository;
import com.edugauge.repositiry.DailyTodoRecordRepository;
import com.edugauge.repositiry.StudyRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.edugauge.repositiry.ProofImageRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final StudyRecordRepository studyRecordRepository;
    private final ProofImageRepository proofImageRepository;
    private final DailyProgressRepository dailyProgressRepository;
    private final DailyTodoRecordRepository dailyTodoRecordRepository;

    public CalendarDetailResponse getCalendarDetail(Long userId, LocalDate date){
        Optional<StudyRecord> studyRecord =
                studyRecordRepository.findByUser_IdAndStudyDate(userId,date);

        long studySeconds = studyRecord.map(StudyRecord::getStudySeconds)
                .orElse(0L);
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
        List<DailyTodoRecord> todoRecords =
                dailyTodoRecordRepository.findByUser_IdAndRecordDate(
                        userId,
                        date
                );
        List<CalendarTodoResponse> todos = todoRecords.stream()
                .map(record -> new CalendarTodoResponse(
                        record.getTodoId(),
                        record.getTitle(),
                        record.getCategoryName(),
                        record.isCompleted()
                ))
                .toList();
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
    public List<CalendarDayResponse>getCalendarMonth(
            Long userId,
            int year,
            int month
    ) {
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
