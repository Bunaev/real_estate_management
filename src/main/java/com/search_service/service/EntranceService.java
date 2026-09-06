package com.search_service.service;

import com.search_service.dto.in.EntranceDTO;
import com.search_service.dto.out.EntranceShortDTO;
import com.search_service.entity.Building;
import com.search_service.entity.Entrance;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.EntranceMapper;
import com.search_service.repository.BuildingRepo;
import com.search_service.repository.EntranceRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EntranceService {
    private final EntranceRepo entranceRepo;
    private final BuildingRepo buildingRepo;
    private final EntranceMapper entranceMapper;

    @Transactional
    public EntranceShortDTO create(Long buildingId, EntranceDTO entranceDTO) {
        Building building = buildingRepo.findById(buildingId)
                .orElseThrow(() -> new EntityNotFoundException("Корпус", buildingId));
        Entrance entrance = entranceMapper.toEntity(entranceDTO);
        entrance.setBuilding(building);
        return entranceMapper.toShortDto(entranceRepo.save(entrance));
    }

    @Transactional(readOnly = true)
    public List<EntranceShortDTO> findByBuildingId(Long buildingId) {
        return entranceMapper.toShortDtoList(entranceRepo.findByBuilding_Id(buildingId));
    }
}