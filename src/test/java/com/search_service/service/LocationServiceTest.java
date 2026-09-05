package com.search_service.service;

import com.search_service.entity.*;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.repository.*;
import org.junit.jupiter.api.BeforeEach;
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
class LocationServiceTest {

    @Mock
    private LocationRepo locationRepo;

    @InjectMocks
    private LocationService locationService;

    @Test
    void findAll_shouldReturnLocations() {
        Location loc = Location.builder().id(1L).name("Санкт-Петербург").build();
        when(locationRepo.findAll()).thenReturn(List.of(loc));

        List<Location> result = locationService.findAll();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Санкт-Петербург");
    }

    @Test
    void create_shouldSaveLocation() {
        Location loc = Location.builder().id(1L).name("Москва").build();
        when(locationRepo.save(any(Location.class))).thenReturn(loc);

        Location result = locationService.create("Москва");
        assertThat(result.getName()).isEqualTo("Москва");
    }
}