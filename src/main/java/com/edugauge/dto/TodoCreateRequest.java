package com.edugauge.dto;

import com.edugauge.domain.Category;
import com.edugauge.domain.RepeatType;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TodoCreateRequest {
    private Long categoryId;
    private String title;
    private RepeatType repeatType;
    private LocalDate endDate;
}
