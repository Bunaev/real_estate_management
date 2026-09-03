package com.search_service.model.services;

import com.search_service.model.entityes.*;
import com.search_service.model.entityes.dto.ResidentialComplexMapper;
import com.search_service.model.entityes.dto.ResidentialComplexOutMapper;
import com.search_service.model.entityes.dto.in.ResidentialComplexDTO;
import com.search_service.model.entityes.dto.out.ResidentialComplexOutDTO;
import com.search_service.model.entityes.dto.out.ResidentialComplexShortDTO;
import com.search_service.model.repo.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResidentialComplexService {
    private final ResidentialComplexRepo complexRepo;
    private final BuildingRepo buildingRepo;
    private final EntranceRepo entranceRepo;
    private final MetroDistanceRepo metroDistanceRepo;
    private final DistrictRepo districtRepo;
    private final DeveloperRepo developerRepo;
    private final MetroStationRepo metroRepo;
    private final ResidentialComplexMapper mapper;
    private final ResidentialComplexOutMapper outMapper;

    @Transactional
    public ResidentialComplexShortDTO create(ResidentialComplexDTO dto) {
        ResidentialComplex complex = mapper.toEntity(dto);

        District district = districtRepo.findById(dto.getDistrictId())
                .orElseThrow(() -> new RuntimeException("Район не найден"));
        Developer developer = developerRepo.findById(dto.getDeveloperId())
                .orElseThrow(() -> new RuntimeException("Застройщик не найден"));

        complex.setDistrict(district);
        complex.setDeveloper(developer);

        complex = complexRepo.save(complex);

        if (dto.getMetroStations() != null) {
            List<ComplexMetroDistance> metroDistances = new ArrayList<>();
            for (var metroDto : dto.getMetroStations()) {
                MetroStation station = metroRepo.findById(metroDto.getMetroStationId())
                        .orElseThrow(() -> new RuntimeException("Станция метро не найдена"));

                ComplexMetroDistance metroDistance = ComplexMetroDistance.builder()
                        .residentialComplex(complex)
                        .metroStation(station)
                        .distance(metroDto.getDistance())
                        .build();
                metroDistances.add(metroDistance);
            }
            metroDistanceRepo.saveAll(metroDistances);
        }

        if (dto.getBuildings() != null) {
            for (var buildingDto : dto.getBuildings()) {
                Building building = Building.builder()
                        .name(buildingDto.getName())
                        .residentialComplex(complex)
                        .completionDate(buildingDto.getCompletionDate())
                        .keyHandoverDate(buildingDto.getKeyHandoverDate())
                        .build();
                building = buildingRepo.save(building);

                if (buildingDto.getEntrances() != null) {
                    List<Entrance> entrances = new ArrayList<>();
                    for (var entranceDto : buildingDto.getEntrances()) {
                        Entrance entrance = Entrance.builder()
                                .name(entranceDto.getName())
                                .building(building)
                                .maxFloors(entranceDto.getMaxFloor())
                                .build();
                        entrances.add(entrance);
                    }
                    entranceRepo.saveAll(entrances);
                }
            }
        }
        return outMapper.toShortDto(complexRepo.findById(complex.getId()).orElseThrow());
    }

    /*
     * ToDo:
     * Переписать логику получения Entity, для оптимизации запросов БД
     */

    public List<ResidentialComplexOutDTO> findAll() {
        return complexRepo.findAll().stream()
                .map(outMapper::toDto)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        complexRepo.deleteById(id);
    }
}
