package com.search_service.repository;

import com.search_service.entity.ComplexMetroDistance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetroDistanceRepo extends JpaRepository<ComplexMetroDistance, Long> {
}