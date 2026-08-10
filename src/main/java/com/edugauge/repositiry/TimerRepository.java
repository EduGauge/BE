package com.edugauge.repositiry;


import com.edugauge.domain.timer.Timer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.support.Repositories;

import java.sql.Time;
import java.util.Optional;

public interface TimerRepository extends JpaRepository<Timer, Long> {
    Optional<Timer> findByUser_Id(Long userId);
}
