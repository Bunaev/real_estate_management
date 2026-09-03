package com.search_service.model.services;

import com.search_service.model.entityes.Building;
import com.search_service.model.entityes.Entrance;
import com.search_service.model.entityes.ResidentialComplex;
import com.search_service.model.entityes.dto.BuildingMapper;
import com.search_service.model.entityes.dto.EntranceMapper;
import com.search_service.model.entityes.dto.ResidentialComplexMapper;
import com.search_service.model.entityes.dto.in.BuildingDTO;
import com.search_service.model.entityes.dto.in.EntranceDTO;
import com.search_service.model.repo.BuildingRepo;
import com.search_service.model.repo.ResidentialComplexRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {
    private final BuildingRepo buildingRepo;
    private final BuildingMapper buildingMapper;
    private final EntranceMapper entranceMapper;
    private final ResidentialComplexRepo complexRepo;

    @Transactional
    public Building create(BuildingDTO buildingDTO, Long residentialComplexId) {
        ResidentialComplex complex = complexRepo.findById(residentialComplexId)
                .orElseThrow(() -> new RuntimeException("ЖК с ID " + residentialComplexId + " не найден"));

        Building building = buildingMapper.toEntity(buildingDTO);
        building.setResidentialComplex(complex);
        if (buildingDTO.getEntrances() != null) {
            List<Entrance> entrances = new ArrayList<>();
            for (var entranceDto : buildingDTO.getEntrances()) {
                Entrance entrance = entranceMapper.toEntity(entranceDto);
                entrance.setBuilding(building);
                entrances.add(entrance);
            }
            building.setEntrances(entrances);
        }

        return buildingRepo.save(building);
    }

    public List<Building> findByComplexId(Long complexId) {
        return buildingRepo.findByResidentialComplex_Id(complexId);
    }

}