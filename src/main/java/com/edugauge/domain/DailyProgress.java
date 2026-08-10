package com.edugauge.domain;

import com.edugauge.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "daily_progress",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_daily_progress_user_date",
                        columnNames = {"user_id", "progress_date"})})
public class DailyProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_progress_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "progress_date", nullable = false)
    private LocalDate progressDate;

    @Column(name = "base_todo_count", nullable = false)
    private int baseTodoCount;

    @Column(name = "completion_bonus_received", nullable = false)
    private boolean completionBonusReceived;

    @Column(name = "completed_todo_count", nullable = false)
    private int completedTodoCount;
    public DailyProgress(
            User user,
            LocalDate progressDate,
            int baseTodoCount
    ) {
        this.user = user;
        this.progressDate = progressDate;
        this.baseTodoCount = baseTodoCount;
        this.completedTodoCount = 0;
        this.completionBonusReceived = false;
    }
    public void completeTodo() {
        this.completedTodoCount++;
    }
    public void addBaseTodo() {
        this.baseTodoCount++;
    }

    public void removeBaseTodo() {
        if (this.baseTodoCount > 0) {
            this.baseTodoCount--;
        }
    }
    public int calculateGauge() {

        if (baseTodoCount == 0) {
            return 0;
        }

        if (completedTodoCount <= baseTodoCount) {
            return completedTodoCount * 100 / baseTodoCount;
        }

        int extraCompleted =
                completedTodoCount - baseTodoCount;

        int gauge =
                100 + (extraCompleted * 5);

        return Math.min(gauge, 120);
    }
    public CharacterFace calculateCharacterFace() {
        int gauge = calculateGauge();

        if (gauge < 20) {
            return CharacterFace.VERY_SAD;
        }

        if (gauge < 40) {
            return CharacterFace.SAD;
        }

        if (gauge < 60) {
            return CharacterFace.NORMAL;
        }

        if (gauge < 100) {
            return CharacterFace.HAPPY;
        }

        return CharacterFace.VERY_HAPPY;
    }
    public boolean isCompletedToday() {
        return calculateGauge() >= 100;
    }

    public boolean receiveCompletionBonusIfPossible() {
        if (calculateGauge() < 100) {
            return false;
        }

        if (completionBonusReceived) {
            return false;
        }

        this.completionBonusReceived = true;
        return true;
    }

}
