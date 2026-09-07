package com.search_service.service;

import com.search_service.dto.in.*;
import com.search_service.dto.out.*;
import com.search_service.entity.*;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.*;
import com.search_service.repository.*;
import com.search_service.specification.SpecificationBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private final DistrictRepo districtRepo;
    private final DeveloperRepo developerRepo;
    private final MetroStationRepo metroRepo;
    private final ResidentialComplexMapper mapper;
    private final BuildingMapper buildingMapper;
    private final SpecificationBuilder specificationBuilder;


    @Transactional
    public ResidentialComplexShortDTO create(ResidentialComplexDTO dto) {
        // 1. Создаём ЖК
        ResidentialComplex complex = mapper.toEntity(dto);

        // 2. Устанавливаем связи
        complex.setDistrict(districtRepo.findById(dto.getDistrictId()).orElseThrow());
        complex.setDeveloper(developerRepo.findById(dto.getDeveloperId()).orElseThrow());

        // 3. Создаём метро и добавляем в коллекцию
        if (dto.getMetroStations() != null) {
            List<ComplexMetroDistance> metroDistances = dto.getMetroStations().stream()
                    .map(metroDto -> {
                        MetroStation station = metroRepo.findById(metroDto.getMetroStationId())
                                .orElseThrow(() -> new EntityNotFoundException("Станция метро", metroDto.getMetroStationId()));
                        return ComplexMetroDistance.builder()
                                .residentialComplex(complex)
                                .metroStation(station)
                                .distance(metroDto.getDistance())
                                .build();
                    })
                    .collect(Collectors.toList());
            complex.setMetroDistances(metroDistances);
        }

        // 4. Создаём корпуса и секции, добавляем в коллекцию
        if (dto.getBuildings() != null) {
            List<Building> buildings = dto.getBuildings().stream()
                    .map(bDto -> {
                        Building building = buildingMapper.toEntity(bDto);
                        building.setResidentialComplex(complex);

                        if (bDto.getEntrances() != null) {
                            List<Entrance> entrances = bDto.getEntrances().stream()
                                    .map(eDto -> Entrance.builder()
                                            .name(eDto.getName())
                                            .maxFloors(eDto.getMaxFloor())
                                            .building(building)
                                            .build())
                                    .collect(Collectors.toList());
                            building.setEntrances(entrances);
                        }
                        return building;
                    })
                    .collect(Collectors.toList());
            complex.setBuildings(buildings);
        }

        // 5. ОДИН save — всё остальное через каскады!
        ResidentialComplex saved = complexRepo.save(complex);
        return mapper.toShortDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ResidentialComplexOutDTO> findAllLightweight(FilterDTO filter, Pageable page) {
        Specification<ResidentialComplex> specification = specificationBuilder.buildComplexes(filter);

        // Получаем Page напрямую, не конвертируя в List
        Page<ResidentialComplex> complexPage = complexRepo.findAll(specification, page);

        // Конвертируем содержимое Page в DTO
        List<ResidentialComplexOutDTO> dtoList = complexPage.getContent().stream()
                .map(mapper::toOutDto)
                .collect(Collectors.toList());

        // Получаем ID для метро
        List<Long> ids = dtoList.stream()
                .map(ResidentialComplexOutDTO::getId)
                .collect(Collectors.toList());

        // Загружаем метро только если есть ID
        if (!ids.isEmpty()) {
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

            // Добавляем метро к каждому DTO
            dtoList.forEach(dto ->
                    dto.setMetroDistances(metroMap.getOrDefault(dto.getId(), List.of()))
            );
        }

        // Возвращаем новый Page с теми же данными пагинации
        return new PageImpl<>(dtoList, complexPage.getPageable(), complexPage.getTotalElements());
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
        return mapper.toDetailDto(complex);
    }

    @Transactional(readOnly = true)
    public ResidentialComplexEditDTO findForEdit(Long id) {
        ResidentialComplex complex = complexRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ЖК не найден"));

        return mapper.toEditDto(complex);
    }

    @Transactional
    public ResidentialComplexOutDTO update(@Valid ResidentialComplexDTO dto) {
        // 1. Загружаем ЖК
        ResidentialComplex complex = complexRepo.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("ЖК не найден", dto.getId()));

        // 2. Обновляем поля
        complex.setName(dto.getName());
        complex.setAddress(dto.getAddress());
        complex.setDeveloper(developerRepo.findById(dto.getDeveloperId()).orElseThrow());
        complex.setDistrict(districtRepo.findById(dto.getDistrictId()).orElseThrow());

        // 3. Обновляем метро (отдельно, т.к. это ManyToMany)
        updateMetro(complex, dto.getMetroStations());

        // 4. Обновляем корпуса и секции (через коллекцию)
        updateBuildings(complex, dto.getBuildings());

        // 5. ОДИН save — всё остальное через каскады!
        ResidentialComplex saved = complexRepo.save(complex);
        return mapper.toOutDto(saved);
    }

    private void updateBuildings(ResidentialComplex complex, List<BuildingDTO> buildingDTOs) {
        // Очищаем коллекцию — Hibernate удалит всё благодаря orphanRemoval
        complex.getBuildings().clear();

        if (buildingDTOs != null) {
            for (BuildingDTO bDto : buildingDTOs) {
                Building building = buildingMapper.toEntity(bDto);
                building.setResidentialComplex(complex);

                // Секции создаются автоматически через каскад
                if (bDto.getEntrances() != null) {
                    List<Entrance> entrances = bDto.getEntrances().stream()
                            .map(eDto -> Entrance.builder()
                                    .name(eDto.getName())
                                    .maxFloors(eDto.getMaxFloor())
                                    .building(building)
                                    .build())
                            .collect(Collectors.toList());
                    building.setEntrances(entrances);
                }

                complex.getBuildings().add(building);
            }
        }
    }

    private void updateMetro(ResidentialComplex complex, List<MetroDistanceDTO> metroDTOs) {
        complex.getMetroDistances().clear();

        if (metroDTOs != null && !metroDTOs.isEmpty()) {
            List<ComplexMetroDistance> metroDistances = new ArrayList<>();

            for (MetroDistanceDTO metroDto : metroDTOs) {
                MetroStation station = metroRepo.findById(metroDto.getMetroStationId())
                        .orElseThrow(() -> new EntityNotFoundException("Станция метро", metroDto.getMetroStationId()));

                ComplexMetroDistance metroDistance = ComplexMetroDistance.builder()
                        .residentialComplex(complex)
                        .metroStation(station)
                        .distance(metroDto.getDistance())
                        .build();

                metroDistances.add(metroDistance);
            }

            complex.getMetroDistances().addAll(metroDistances);
        }
    }
}