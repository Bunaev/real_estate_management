package com.search_service.service;

import com.search_service.entity.MetroStation;
import com.search_service.repository.MetroStationRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MetroServiceTest {

    @Mock
    private MetroStationRepo metroRepo;

    @InjectMocks
    private MetroService metroService;

    @Test
    void findAll_shouldReturnStations() {
        MetroStation station = MetroStation.builder().id(1L).name("Невский проспект").build();
        when(metroRepo.findAll()).thenReturn(List.of(station));

        List<MetroStation> result = metroService.findAll();
        assertThat(result).hasSize(1);
    }

    @Test
    void create_shouldSaveStation() {
        MetroStation station = MetroStation.builder().id(1L).name("Новая станция").build();
        when(metroRepo.save(any(MetroStation.class))).thenReturn(station);

        MetroStation result = metroService.create("Новая станция");
        assertThat(result.getName()).isEqualTo("Новая станция");
    }
}