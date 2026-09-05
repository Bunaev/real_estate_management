package com.search_service.service;

import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.ResidentialComplexShortDTO;
import com.search_service.entity.*;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.*;
import com.search_service.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResidentialComplexServiceTest {

    @Mock private ResidentialComplexRepo complexRepo;
    @Mock private BuildingRepo buildingRepo;
    @Mock private EntranceRepo entranceRepo;
    @Mock private MetroDistanceRepo metroDistanceRepo;
    @Mock private DistrictRepo districtRepo;
    @Mock private DeveloperRepo developerRepo;
    @Mock private MetroStationRepo metroRepo;
    @Mock private ResidentialComplexMapper mapper;
    @Mock private ResidentialComplexOutMapper outMapper;
    @Mock private BuildingMapper buildingMapper;

    @InjectMocks
    private ResidentialComplexService service;

    @Test
    void create_shouldSaveComplexAndReturnShortDto() {
        ResidentialComplexDTO dto = ResidentialComplexDTO.builder()
                .name("ЖК Невский")
                .address("ул. Невская, 1")
                .districtId(1L)
                .developerId(1L)
                .build();

        ResidentialComplex complex = ResidentialComplex.builder()
                .id(1L)
                .name("ЖК Невский")
                .build();

        ResidentialComplex savedComplex = ResidentialComplex.builder()
                .id(1L)
                .name("ЖК Невский")
                .district(District.builder().id(1L).name("Приморский")
                        .location(Location.builder().id(1L).name("СПб").build()).build())
                .developer(Developer.builder().id(1L).name("Dev").build())
                .build();

        ResidentialComplexShortDTO shortDto = ResidentialComplexShortDTO.builder()
                .id(1L).name("ЖК Невский").developer("Dev").fullAddress("СПб, Приморский район, ул. Невская, 1").build();

        when(mapper.toEntity(any(ResidentialComplexDTO.class))).thenReturn(complex);
        when(districtRepo.findById(1L)).thenReturn(Optional.of(District.builder().id(1L).build()));
        when(developerRepo.findById(1L)).thenReturn(Optional.of(Developer.builder().id(1L).build()));
        when(complexRepo.save(any(ResidentialComplex.class))).thenReturn(complex);
        when(complexRepo.findById(1L)).thenReturn(Optional.of(savedComplex));
        when(outMapper.toShortDto(any(ResidentialComplex.class))).thenReturn(shortDto);

        ResidentialComplexShortDTO result = service.create(dto);
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("ЖК Невский");
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(complexRepo.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("ЖК");
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
        when(complexRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }
}