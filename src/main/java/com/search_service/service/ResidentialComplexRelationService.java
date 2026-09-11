package com.search_service.service;

import com.search_service.dto.in.BuildingDTO;
import com.search_service.dto.in.MetroDistanceDTO;
import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.entity.Building;
import com.search_service.entity.ComplexMetroDistance;
import com.search_service.entity.Entrance;
import com.search_service.entity.MetroStation;
import com.search_service.entity.ResidentialComplex;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.BuildingMapper;
import com.search_service.repository.MetroStationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис сборки связей ЖК: метро, корпуса, секции.
 * Вынесен из ResidentialComplexService, чтобы там не было ручной сборки графа.
 */
@Service
@RequiredArgsConstructor
public class ResidentialComplexRelationService {

    private final MetroStationRepo metroRepo;
    private final BuildingMapper buildingMapper;

    /** Заполняет связи для нового ЖК. */
    public void applyRelations(ResidentialComplex complex, ResidentialComplexDTO dto) {
        setMetroDistances(complex, dto.getMetroStations());
        setBuildings(complex, dto.getBuildings());
    }

    /** Обновляет связи существующего ЖК. */
    public void updateRelations(ResidentialComplex complex, ResidentialComplexDTO dto) {
        updateMetro(complex, dto.getMetroStations());
        updateBuildings(complex, dto.getBuildings());
    }

    private void setMetroDistances(ResidentialComplex complex, List<MetroDistanceDTO> metroDTOs) {
        if (metroDTOs == null) return;
        List<ComplexMetroDistance> metroDistances = metroDTOs.stream()
                .map(metroDto -> buildMetroDistance(complex, metroDto))
                .collect(Collectors.toList());
        complex.setMetroDistances(metroDistances);
    }

    private void setBuildings(ResidentialComplex complex, List<BuildingDTO> buildingDTOs) {
        if (buildingDTOs == null) return;
        List<Building> buildings = buildingDTOs.stream()
                .map(bDto -> buildBuilding(complex, bDto))
                .collect(Collectors.toList());
        complex.setBuildings(buildings);
    }

    private void updateMetro(ResidentialComplex complex, List<MetroDistanceDTO> metroDTOs) {
        complex.getMetroDistances().clear();
        if (metroDTOs == null || metroDTOs.isEmpty()) return;
        List<ComplexMetroDistance> metroDistances = metroDTOs.stream()
                .map(metroDto -> buildMetroDistance(complex, metroDto))
                .toList();
        complex.getMetroDistances().addAll(metroDistances);
    }

    private void updateBuildings(ResidentialComplex complex, List<BuildingDTO> buildingDTOs) {
        complex.getBuildings().clear();
        if (buildingDTOs == null) return;
        for (BuildingDTO bDto : buildingDTOs) {
            complex.getBuildings().add(buildBuilding(complex, bDto));
        }
    }

    private ComplexMetroDistance buildMetroDistance(ResidentialComplex complex, MetroDistanceDTO dto) {
        MetroStation station = metroRepo.findById(dto.getMetroStationId())
                .orElseThrow(() -> new EntityNotFoundException("Станция метро", dto.getMetroStationId()));
        return ComplexMetroDistance.builder()
                .residentialComplex(complex)
                .metroStation(station)
                .distance(dto.getDistance())
                .build();
    }

    private Building buildBuilding(ResidentialComplex complex, BuildingDTO bDto) {
        Building building = buildingMapper.toEntity(bDto);
        building.setResidentialComplex(complex);
        if (bDto.getEntrances() != null) {
            List<Entrance> entrances = bDto.getEntrances().stream()
                    .map(eDto -> Entrance.builder()
                            .name(eDto.getName())
                            .maxFloors(eDto.getMaxFloor())
                            .building(building)
                            .build())
                    .toList();
            building.setEntrances(entrances);
        }
        return building;
    }
}