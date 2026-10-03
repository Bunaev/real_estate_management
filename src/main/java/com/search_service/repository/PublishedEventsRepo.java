package com.search_service.repository;

import com.search_service.entity.PublishedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PublishedEventsRepo extends JpaRepository<PublishedEvent, Long> {

    @Query("SELECT e FROM PublishedEvent e WHERE e.startEventDate <= :now AND e.endEventDate >= :now")
    List<PublishedEvent> findActiveEvents(@Param("now") LocalDate now);
}
