package com.edugauge.repositiry;

import com.edugauge.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByUser_Id(Long userId);
    long countByUser_Id(Long userId);
    List<Todo> findByCategory_IdAndUser_Id(Long categoryId, Long userId);
    void deleteByUser_Id(Long userId);
}
