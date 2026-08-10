package com.edugauge.repositiry;

import com.edugauge.domain.user.DailyTodoRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface DailyTodoRecordRepository
        extends JpaRepository<DailyTodoRecord, Long> {

    List<DailyTodoRecord> findByUser_IdAndRecordDate(
            Long userId,
            LocalDate recordDate
    );
    boolean existsByUser_IdAndRecordDate(
            Long userId,
            LocalDate recordDate
    );
    void deleteByUser_Id(Long userId);
}
