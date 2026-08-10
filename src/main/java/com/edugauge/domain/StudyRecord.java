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
@Table(name = "study_records",uniqueConstraints = {
        @UniqueConstraint( name = "unique_key_ record", columnNames = {"user_id", "study_date"})
})
public class StudyRecord {
    @Id
    @Column(name = "record_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "study_date", nullable = false)
    private LocalDate studyDate;

    @Column(name = "study_seconds", nullable = false)
    private long studySeconds;

    public StudyRecord(
            User user,
            LocalDate studyDate,
            long studySeconds
    ){
        this.user = user;
        this.studyDate = studyDate;
        this.studySeconds = studySeconds;
    }
    public void addStudySeconds(long seconds) {
        this.studySeconds += seconds;
    }


}
