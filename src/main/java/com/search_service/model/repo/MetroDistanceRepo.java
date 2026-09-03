package com.search_service.model.repo;

import com.search_service.model.entityes.ComplexMetroDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetroDistanceRepo extends JpaRepository<ComplexMetroDistance, Long> {
}
