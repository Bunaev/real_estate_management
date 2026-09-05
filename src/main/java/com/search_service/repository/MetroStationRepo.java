package com.search_service.repository;

import com.search_service.entity.MetroStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetroStationRepo extends JpaRepository<MetroStation, Long> {
}