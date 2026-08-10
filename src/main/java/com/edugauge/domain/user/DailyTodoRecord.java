package com.edugauge.domain.user;

import com.edugauge.domain.Todo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "daily_todo_records")
public class DailyTodoRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_todo_record_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "todo_id", nullable = false)
    private Long todoId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "category_name", nullable = false)
    private String categoryName;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    public DailyTodoRecord(
            User user,
            LocalDate recordDate,
            Todo todo
    ) {
        this.user = user;
        this.recordDate = recordDate;
        this.todoId = todo.getId();
        this.title = todo.getTitle();
        this.categoryName = todo.getCategory().getName();
        this.completed = todo.isCompleted();
    }

}
