package com.search_service.model.repo;

import com.search_service.model.entityes.MetroStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetroStationRepo extends JpaRepository<MetroStation, Long> {
}
