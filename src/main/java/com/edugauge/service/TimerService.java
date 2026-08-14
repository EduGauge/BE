package com.edugauge.service;

import com.edugauge.domain.StudyRecord;
import com.edugauge.domain.timer.Timer;
import com.edugauge.domain.timer.TimerStatus;
import com.edugauge.domain.user.User;
import com.edugauge.dto.TimerResponse;
import com.edugauge.repositiry.StudyRecordRepository;
import com.edugauge.repositiry.TimerRepository;
import com.edugauge.repositiry.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class TimerService {
    private final TimerRepository timerRepository;
    private final UserRepository userRepository;
    private final StudyRecordRepository studyRecordRepository;
    private final StudyDateService studyDateService;


    private LocalDateTime calculateResetAt(LocalDateTime now) {
        return studyDateService.getNextResetAt(now);
    }

    public void startTimer(Long userId) {
        Optional<Timer> timer = timerRepository.findByUser_Id(userId);
        LocalDateTime now = LocalDateTime.now();
        if (timer.isEmpty()) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

            LocalDateTime resetAt = calculateResetAt(now);
            Timer newTimer = new Timer(
                    user, now, resetAt
            );
            timerRepository.save(newTimer);
            return;

        }

        Timer existingTimer = timer.get();


        if (needsReset(existingTimer, now)) {
            handleReset(existingTimer, userId, now);
        }


        if (existingTimer.getTimerStatus() == TimerStatus.RUNNING) {
            throw new IllegalArgumentException("이미 실행중인 타이머입니다");
        }
        if (existingTimer.getTimerStatus() == TimerStatus.PAUSED) {
            existingTimer.resume(now);
            return;
        }
        if (existingTimer.getTimerStatus() == TimerStatus.STOPPED) {
            LocalDateTime resetAt = calculateResetAt(now);
            existingTimer.restart(now, resetAt);

        }


    }

    public void pauseTimer(Long userId) {

        Timer timer = timerRepository.findByUser_Id(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("타이머가 존재하지 않습니다")
                );

        LocalDateTime now = LocalDateTime.now();

        if (needsReset(timer, now)) {
            handleReset(timer, userId, now);
        }

        if (timer.getTimerStatus() != TimerStatus.RUNNING) {
            throw new IllegalArgumentException("실행 중인 타이머가 아닙니다");
        }

        Duration duration = Duration.between(
                timer.getStartedAt(),
                now
        );

        long seconds = duration.getSeconds();

        timer.pause(seconds);
    }

    public void stopTimer(Long userId) {

        Timer timer = timerRepository.findByUser_Id(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("타이머가 존재하지 않습니다")
                );

        if (timer.getTimerStatus() == TimerStatus.STOPPED) {
            throw new IllegalArgumentException("이미 정지된 타이머입니다");
        }

        LocalDateTime now = LocalDateTime.now();

        if (needsReset(timer, now)) {
            handleReset(timer, userId, now);

            if (timer.getTimerStatus() == TimerStatus.STOPPED) {
                return;
            }
        }

        if (timer.getTimerStatus() == TimerStatus.RUNNING) {

            Duration duration = Duration.between(
                    timer.getStartedAt(),
                    now
            );

            long seconds = duration.getSeconds();

            timer.stop(seconds);

        } else if (timer.getTimerStatus() == TimerStatus.PAUSED) {
            timer.pausedStop();
        }

        LocalDate studyDate = studyDateService.getStudyDate(now);

        saveStudyRecord(
                timer,
                userId,
                studyDate,
                timer.getAccumulatedSeconds()
        );
    }

    public TimerResponse getTimer(Long userId) {

        Timer timer = timerRepository.findByUser_Id(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("타이머가 존재하지 않습니다")
                );

        LocalDateTime now = LocalDateTime.now();

        if (needsReset(timer, now)) {
            handleReset(timer, userId, now);
        }

        long elapsedSeconds = timer.getAccumulatedSeconds();

        if (timer.getTimerStatus() == TimerStatus.RUNNING) {

            Duration duration = Duration.between(
                    timer.getStartedAt(),
                    now
            );

            elapsedSeconds += duration.getSeconds();
        }

        return new TimerResponse(
                timer.getTimerStatus(),
                elapsedSeconds
        );
    }
    private boolean needsReset(Timer timer, LocalDateTime now) {
        return !now.isBefore(timer.getResetAt());
    }
    private void saveStudyRecord(
            Timer timer,
            Long userId,
            LocalDate studyDate,
            long seconds
    ) {
        Optional<StudyRecord> studyRecord =
                studyRecordRepository.findByUser_IdAndStudyDate(
                        userId,
                        studyDate
                );

        if (studyRecord.isPresent()) {
            StudyRecord record = studyRecord.get();
            record.addStudySeconds(seconds);

        } else {
            StudyRecord newRecord = new StudyRecord(
                    timer.getUser(),
                    studyDate,
                    seconds
            );

            studyRecordRepository.save(newRecord);
        }
    }

    private void handleReset(
            Timer timer,
            Long userId,
            LocalDateTime now
    ) {
        LocalDateTime resetAt = timer.getResetAt();


        if (timer.getTimerStatus() == TimerStatus.RUNNING) {

            Duration beforeReset = Duration.between(
                    timer.getStartedAt(),
                    resetAt
            );

            long secondsBeforeReset = beforeReset.getSeconds();

            long totalBeforeReset =
                    timer.getAccumulatedSeconds() + secondsBeforeReset;
            LocalDate studyDate = studyDateService.getStudyDate(
                    resetAt.minusNanos(1)
            );

            saveStudyRecord(
                    timer,
                    userId,
                    studyDate,
                    totalBeforeReset
            );

            Duration afterReset = Duration.between(
                    resetAt,
                    now
            );

            long secondsAfterReset = afterReset.getSeconds();

            LocalDateTime nextResetAt = calculateResetAt(now);

            timer.resetRunning(
                    now,
                    nextResetAt,
                    secondsAfterReset
            );

            return;
        }


        if (timer.getTimerStatus() == TimerStatus.PAUSED) {

            LocalDate studyDate = studyDateService.getStudyDate(
                    resetAt.minusNanos(1)
            );

            saveStudyRecord(
                    timer,
                    userId,
                    studyDate,
                    timer.getAccumulatedSeconds()
            );

            LocalDateTime nextResetAt = calculateResetAt(now);

            timer.reset(nextResetAt);

            return;
        }

        if (timer.getTimerStatus() == TimerStatus.STOPPED) {

            LocalDateTime nextResetAt = calculateResetAt(now);

            timer.reset(nextResetAt);
        }
    }

}
