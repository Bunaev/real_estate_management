package com.search_service.service;

import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.*;
import com.search_service.entity.*;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.BuildingMapper;
import com.search_service.mapper.ResidentialComplexMapper;
import com.search_service.mapper.ResidentialComplexOutMapper;
import com.search_service.repository.*;
import com.search_service.specification.ResidentialComplexSpecificationBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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
    private final BuildingMapper buildingMapper;
    private final ResidentialComplexSpecificationBuilder specificationBuilder;


    @Transactional
    public ResidentialComplexShortDTO create(ResidentialComplexDTO dto) {
        ResidentialComplex complex = mapper.toEntity(dto);

        District district = districtRepo.findById(dto.getDistrictId())
                .orElseThrow(() -> new EntityNotFoundException("Район", dto.getDistrictId()));
        Developer developer = developerRepo.findById(dto.getDeveloperId())
                .orElseThrow(() -> new EntityNotFoundException("Застройщик", dto.getDeveloperId()));

        complex.setDistrict(district);
        complex.setDeveloper(developer);
        complex = complexRepo.save(complex);

        if (dto.getMetroStations() != null) {
            List<ComplexMetroDistance> metroDistances = new ArrayList<>();
            for (var metroDto : dto.getMetroStations()) {
                MetroStation station = metroRepo.findById(metroDto.getMetroStationId())
                        .orElseThrow(() -> new EntityNotFoundException("Станция метро", metroDto.getMetroStationId()));

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

    @Transactional(readOnly = true)
    public List<ResidentialComplexOutDTO> findAllLightweight(FilterDTO filter, Pageable page) {
        Specification<ResidentialComplex> specification = specificationBuilder.build(filter);
        List<ResidentialComplexOutDTO> dtoList = complexRepo.findAll(specification, page)
                .stream().map(outMapper::toDto).toList();
        List<Long> ids = dtoList.stream()
                .map(ResidentialComplexOutDTO::getId)
                .toList();
        List<Object[]> metroData = complexRepo.findMetroDistancesByComplexIds(ids);

        Map<Long, List<MetroDistanceOutDTO>> metroMap = metroData.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(
                                row -> MetroDistanceOutDTO.builder()
                                        .stationName((String) row[1])
                                        .distance((Integer) row[2])
                                        .build(),
                                Collectors.toList()
                        )
                ));

        dtoList.forEach(dto ->
                dto.setMetroDistances(metroMap.getOrDefault(dto.getId(), List.of()))
        );
        return dtoList;
    }

    @Transactional
    public void delete(Long id) {
        if (!complexRepo.existsById(id)) {
            throw new EntityNotFoundException("ЖК", id);
        }
        complexRepo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public ResidentialComplexDetailDTO findById(Long id) {
        ResidentialComplex complex = complexRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ЖК", id));
        return outMapper.toDetailDto(complex, buildingMapper);
    }
}