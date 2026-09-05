package com.search_service.service;

import com.search_service.dto.in.BuildingDTO;
import com.search_service.entity.Building;
import com.search_service.entity.Entrance;
import com.search_service.entity.ResidentialComplex;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.BuildingMapper;
import com.search_service.mapper.EntranceMapper;
import com.search_service.repository.BuildingRepo;
import com.search_service.repository.ResidentialComplexRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new EntityNotFoundException("ЖК", residentialComplexId));

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

    @Transactional(readOnly = true)
    public List<Building> findByComplexId(Long complexId) {
        return buildingRepo.findByResidentialComplex_Id(complexId);
    }
}