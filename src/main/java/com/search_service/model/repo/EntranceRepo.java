package com.search_service.model.repo;

import com.search_service.model.entityes.Entrance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntranceRepo extends JpaRepository<Entrance, Long> {
    List<Entrance> findByBuilding_Id(Long buildingId);
}
