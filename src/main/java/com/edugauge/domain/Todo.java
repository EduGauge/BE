package com.edugauge.domain;

import com.edugauge.domain.user.User;
import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false, length = 20)
    private String title;

    @Column(name = "repeat_type", nullable = true)
    @Enumerated(EnumType.STRING)
    private RepeatType repeatType;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name = "study_seconds", nullable = false)
    private int studySeconds;

    public Todo(Category category, User user, String title, RepeatType repeatType, LocalDate endDate){
        this.category = category;
        this.user = user;
        this.title = title;
        this.repeatType = repeatType == null? RepeatType.NONE : repeatType;
        this.endDate = endDate;
        this.completed = false;
        this.studySeconds = 0;
    }

    public void update(
            Category category,
            String title,
            RepeatType repeatType,
            LocalDate endDate
    ){
        this.category = category;
        this.title = title;
        this.repeatType = repeatType;
        this.endDate = endDate;
    }
    public void complete(){
        this.completed = true;
    }

    public void resetCompletion(){
        this.completed = false;
    }

}
