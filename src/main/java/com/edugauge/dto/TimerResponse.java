package com.edugauge.dto;

import com.edugauge.domain.timer.TimerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TimerResponse {
    private TimerStatus status;
    private long elapsedSeconds;
}
