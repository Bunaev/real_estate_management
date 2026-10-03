package com.search_service.repository;

import com.search_service.entity.ScheduledTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduledTaskRepo extends JpaRepository<ScheduledTask, Long> {

    @Query("SELECT s FROM ScheduledTask s WHERE s.status = 'PLANNED' AND s.deadLine <= :now")
    List<ScheduledTask> findScheduledTasks(@Param("now") LocalDateTime nowLocalDateTime);
}
