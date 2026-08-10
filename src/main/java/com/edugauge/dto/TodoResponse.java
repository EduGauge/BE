package com.edugauge.dto;

import com.edugauge.domain.RepeatType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TodoResponse {
    private Long todoId;
    private String title;
    private Long categoryId;
    private boolean completed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDate endDate;
    private String categoryName;
    private RepeatType repeatType;

}
