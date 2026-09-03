package com.search_service.model.services;

import com.search_service.model.entityes.Building;
import com.search_service.model.entityes.Entrance;
import com.search_service.model.repo.BuildingRepo;
import com.search_service.model.repo.EntranceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntranceService {
    private final EntranceRepo entranceRepo;
    private final BuildingRepo buildingRepo;

    public Entrance create(String name, Long buildingId, Integer maxFloor) {
        Building building = this.buildingRepo.findById(buildingId).orElseThrow(() -> new RuntimeException("Такого корпуса не существует."));
        Entrance entrance = Entrance.builder().name(name).building(building).maxFloors(maxFloor).build();
        return entranceRepo.save(entrance);
    }

    public List<Entrance> findByBuildingId(Long buildingId) {
        return entranceRepo.findByBuilding_Id(buildingId);
    }
}
