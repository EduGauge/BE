package com.edugauge.repositiry;

import com.edugauge.domain.StudyRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface StudyRecordRepository extends JpaRepository<StudyRecord, Long> {
    Optional<StudyRecord> findByUser_IdAndStudyDate(
            Long userId,
            LocalDate studyDate
    );
}
