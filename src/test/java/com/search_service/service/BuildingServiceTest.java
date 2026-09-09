package com.search_service.service;

import com.search_service.dto.in.BuildingDTO;
import com.search_service.dto.out.BuildingShortDTO;
import com.search_service.entity.Building;
import com.search_service.entity.Entrance;
import com.search_service.entity.ResidentialComplex;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.BuildingMapper;
import com.search_service.mapper.EntranceMapper;
import com.search_service.repository.BuildingRepo;
import com.search_service.repository.ResidentialComplexRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuildingServiceTest {

    @Mock
    private BuildingRepo buildingRepo;
    @Mock
    private BuildingMapper buildingMapper;
    @Mock
    private EntranceMapper entranceMapper;
    @Mock
    private ResidentialComplexRepo complexRepo;

    @InjectMocks
    private BuildingService buildingService;

    @Test
    void create_shouldSaveBuilding() {
        BuildingDTO dto = BuildingDTO.builder()
                .name("Корпус 1")
                .entrances(List.of(
                        com.search_service.dto.in.EntranceDTO.builder().name("Секция 1").maxFloor(10).build()
                ))
                .build();
        ResidentialComplex complex = ResidentialComplex.builder().id(1L).name("ЖК Невский").build();
        Building building = Building.builder().id(1L).name("Корпус 1").residentialComplex(complex).build();
        BuildingShortDTO shortDto = BuildingShortDTO.builder().id(1L).name("Корпус 1").build();

        when(complexRepo.findById(1L)).thenReturn(Optional.of(complex));
        when(buildingMapper.toEntity(any(BuildingDTO.class))).thenReturn(building);
        when(entranceMapper.toEntity(any(com.search_service.dto.in.EntranceDTO.class)))
                .thenReturn(Entrance.builder().name("Секция 1").maxFloors(10).building(building).build());
        when(buildingRepo.save(any(Building.class))).thenReturn(building);
        when(buildingMapper.toShortDto(any(Building.class))).thenReturn(shortDto);

        BuildingShortDTO result = buildingService.create(dto, 1L);

        assertThat(result.getName()).isEqualTo("Корпус 1");
    }

    @Test
    void create_shouldThrow_whenComplexNotFound() {
        when(complexRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.create(BuildingDTO.builder().build(), 999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
