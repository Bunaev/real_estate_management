package com.search_service.service;

import com.search_service.entity.District;
import com.search_service.entity.Location;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.repository.DistrictRepo;
import com.search_service.repository.LocationRepo;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DistrictServiceTest {

    @Mock
    private DistrictRepo districtRepo;
    @Mock
    private LocationRepo locationRepo;

    @InjectMocks
    private DistrictService districtService;

    @Test
    void findByLocationId_shouldReturnDistricts() {
        District district = District.builder().id(1L).name("Приморский").build();
        when(districtRepo.findByLocation_Id(1L)).thenReturn(List.of(district));

        List<District> result = districtService.findByLocationId(1L);
        assertThat(result).hasSize(1);
    }

    @Test
    void create_shouldSave_whenLocationExists() {
        Location location = Location.builder().id(1L).name("Санкт-Петербург").build();
        District district = District.builder().id(1L).name("Невский").location(location).build();
        when(locationRepo.findById(1L)).thenReturn(Optional.of(location));
        when(districtRepo.save(any(District.class))).thenReturn(district);

        District result = districtService.create("Невский", 1L);
        assertThat(result.getName()).isEqualTo("Невский");
    }

    @Test
    void create_shouldThrow_whenLocationNotFound() {
        when(locationRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> districtService.create("Район", 999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}