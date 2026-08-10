package com.edugauge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoCompleteResponse {
    private Long todoId;
    private boolean completed;
    private int earnedExperience;
    private int level;
    private int experience;
    private boolean levelUp;
}