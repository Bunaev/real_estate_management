package com.search_service.model.repo;

import com.search_service.model.entityes.Developer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeveloperRepo extends JpaRepository<Developer, Long> {
}
