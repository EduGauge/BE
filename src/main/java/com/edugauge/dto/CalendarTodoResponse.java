package com.edugauge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CalendarTodoResponse {
    private Long todoId;
    private String title;
    private String categoryName;
    private boolean completed;

}
