package com.edugauge.dto;

import com.edugauge.domain.CharacterFace;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

import java.util.List;

@Getter
@AllArgsConstructor
public class CalendarDetailResponse {
    private LocalDate date;
    private int gauge;
    private CharacterFace characterFace;
    private String message;
    private long studySeconds;
    private ProofImageResponse proofImage;
    private int totalTodoCount;
    private int completedTodoCount;
    private List<CalendarTodoResponse> todos;

}
