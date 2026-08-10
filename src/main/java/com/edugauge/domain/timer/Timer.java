package com.edugauge.domain.timer;

import com.edugauge.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "timers")
public class Timer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "timer_id")
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TimerStatus timerStatus;
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    @Column(name = "accumulated_seconds", nullable = false)
    private long accumulatedSeconds;
    @Column(name = "reset_at")
    private LocalDateTime resetAt;

    public Timer(User user, LocalDateTime startedAt, LocalDateTime resetAt){
        this.user = user;
        this.startedAt = startedAt;
        this.resetAt = resetAt;
        this.timerStatus = TimerStatus.RUNNING;
        this.accumulatedSeconds = 0;
    }

    public void resume(LocalDateTime now) {
        this.startedAt = now;
        this.timerStatus = TimerStatus.RUNNING;
    }

    public void pause(long seconds){
        this.accumulatedSeconds += seconds;
        this.timerStatus = TimerStatus.PAUSED;
        this.startedAt = null;
    }
    public void stop(long seconds){
        this.accumulatedSeconds += seconds;
        this.timerStatus = TimerStatus.STOPPED;
        this.startedAt = null;
    }
    public void pausedStop() {
        this.timerStatus = TimerStatus.STOPPED;
        this.startedAt = null;
    }

    public void restart(LocalDateTime now, LocalDateTime resetAt){
        this.startedAt = now;
        this.timerStatus = TimerStatus.RUNNING;
        this.resetAt = resetAt;
        this.accumulatedSeconds = 0;
    }
    public void reset(LocalDateTime nextResetAt){
        this.accumulatedSeconds = 0L;
        this.startedAt = null;
        this.timerStatus = TimerStatus.STOPPED;
        this.resetAt = nextResetAt;
    }
    public void resetRunning(
            LocalDateTime now,
            LocalDateTime nextResetAt,
            long secondsAfterReset
    ) {
        this.accumulatedSeconds = secondsAfterReset;
        this.startedAt = now;
        this.timerStatus = TimerStatus.RUNNING;
        this.resetAt = nextResetAt;
    }

}
