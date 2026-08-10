package com.edugauge.dto;

import com.edugauge.domain.CharacterFace;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class CalendarDayResponse {
    private LocalDate date;
    private int gauge;
    private CharacterFace characterFace;

}
