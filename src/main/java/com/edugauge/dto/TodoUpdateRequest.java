package com.edugauge.dto;

import com.edugauge.domain.Category;
import com.edugauge.domain.RepeatType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class TodoUpdateRequest {
    private Long categoryId;
    private String title;
    private RepeatType repeatType;
    private LocalDate endDate;
}
