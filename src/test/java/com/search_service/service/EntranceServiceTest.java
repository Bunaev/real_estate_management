package com.search_service.service;

import com.search_service.dto.in.EntranceDTO;
import com.search_service.entity.Building;
import com.search_service.entity.Entrance;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.repository.BuildingRepo;
import com.search_service.repository.EntranceRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntranceServiceTest {

    @Mock
    private EntranceRepo entranceRepo;
    @Mock
    private BuildingRepo buildingRepo;
    @Mock
    private EntranceMapper entranceMapper;

    @InjectMocks
    private EntranceService entranceService;

    @Test
    void create_shouldSaveEntrance() {
        Building building = Building.builder().id(1L).name("Корпус 1").build();
        EntranceDTO dto = EntranceDTO.builder().name("Секция А").maxFloor(20).build();
        Entrance entrance = Entrance.builder().id(1L).name("Секция А").building(building).build();

        when(buildingRepo.findById(1L)).thenReturn(Optional.of(building));
        when(entranceMapper.toEntity(any(EntranceDTO.class))).thenReturn(entrance);
        when(entranceRepo.save(any(Entrance.class))).thenReturn(entrance);

        Entrance result = entranceService.create(1L, dto);
        assertThat(result.getName()).isEqualTo("Секция А");
    }

    @Test
    void create_shouldThrow_whenBuildingNotFound() {
        when(buildingRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> entranceService.create(999L, EntranceDTO.builder().build()))
                .isInstanceOf(EntityNotFoundException.class);
    }
}