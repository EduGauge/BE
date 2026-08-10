package com.edugauge.repositiry;

import com.edugauge.domain.DailyProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyProgressRepository extends JpaRepository<DailyProgress, Long> {
    Optional<DailyProgress> findByUser_IdAndProgressDate(
            Long userId,
            LocalDate progressDate
    );
    List<DailyProgress> findByUser_IdAndProgressDateBetween(
            Long userId,
            LocalDate startDate,
            LocalDate endDate
    );
}
