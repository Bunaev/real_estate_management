package com.search_service.repository;

import com.search_service.entity.Entrance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntranceRepo extends JpaRepository<Entrance, Long> {
    List<Entrance> findByBuilding_Id(Long buildingId);
    void deleteByBuildingId(Long buildingId);
}