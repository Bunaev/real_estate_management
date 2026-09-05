package com.search_service.service;

import com.search_service.dto.in.ApartmentInDTO;
import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.out.ApartmentDTO;
import com.search_service.entity.*;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.ApartmentMapper;
import com.search_service.repository.ApartmentRepo;
import com.search_service.repository.EntranceRepo;
import com.search_service.specification.SpecificationBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApartmentServiceTest {

    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private EntranceRepo entranceRepo;
    @Mock
    private ApartmentMapper mapper;
    @Mock
    private SpecificationBuilder specificationBuilder;

    @InjectMocks
    private ApartmentService apartmentService;

    private Apartment apartment;
    private ApartmentDTO apartmentDto;

    @BeforeEach
    void setUp() {
        Entrance entrance = new Entrance();
        entrance.setId(1L);
        entrance.setName("Секция 1");

        apartment = Apartment.builder()
                .id(1L)
                .number(10)
                .floor(3)
                .area(45.0)
                .price(5_000_000.0)
                .type(ApartmentType.ONE_ROOM)
                .bathroomType(BathroomType.COMBINED)
                .hasBalcony(true)
                .status(Status.AVAILABLE)
                .entrance(entrance)
                .build();

        apartmentDto = ApartmentDTO.builder()
                .id(1L)
                .number(10)
                .floor(3)
                .area(45.0)
                .price(5_000_000.0)
                .type("ONE_ROOM")
                .bathroomType("COMBINED")
                .hasBalcony(true)
                .status("AVAILABLE")
                .build();
    }

    @Test
    void findByComplexId_shouldReturnDtos() {
        when(apartmentRepo.findByEntrance_Building_ResidentialComplex_Id(1L))
                .thenReturn(List.of(apartment));
        when(mapper.toListDtoList(anyList())).thenReturn(List.of(apartmentDto));

        List<ApartmentDTO> result = apartmentService.findByComplexId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNumber()).isEqualTo(10);
    }

    @Test
    void delete_shouldDelete_whenExists() {
        when(apartmentRepo.existsById(1L)).thenReturn(true);
        apartmentService.delete(1L);
        verify(apartmentRepo).deleteById(1L);
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(apartmentRepo.existsById(999L)).thenReturn(false);
        assertThatThrownBy(() -> apartmentService.delete(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Квартира");
    }

    @Test
    void update_shouldUpdateFields() {
        ApartmentInDTO dto = ApartmentInDTO.builder()
                .id(1L)
                .number(20)
                .floor(5)
                .area(60.0)
                .price(7_000_000.0)
                .type(ApartmentType.TWO_ROOM)
                .bathroomType(BathroomType.SEPARATE)
                .hasBalcony(false)
                .status(Status.RESERVED)
                .build();

        when(apartmentRepo.findById(1L)).thenReturn(Optional.of(apartment));
        when(apartmentRepo.save(any(Apartment.class))).thenReturn(apartment);
        when(mapper.toDto(any(Apartment.class))).thenReturn(apartmentDto);

        ApartmentDTO result = apartmentService.update(dto);

        assertThat(result).isNotNull();
        verify(apartmentRepo).save(apartment);
    }

    @Test
    void update_shouldThrow_whenNotFound() {
        ApartmentInDTO dto = ApartmentInDTO.builder().id(999L).build();
        when(apartmentRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> apartmentService.update(dto))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Квартира");
    }

    @Test
    void getFilteredApartment_shouldReturnPage() {
        FilterDTO filter = FilterDTO.builder().residentialComplexId(1L).build();
        Specification<Apartment> spec = Specification.where(null);
        when(specificationBuilder.build(any(FilterDTO.class))).thenReturn(spec);
        when(mapper.toDto(any(Apartment.class))).thenReturn(apartmentDto);
        when(apartmentRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(apartment)));

        Page<ApartmentDTO> result = apartmentService.getFilteredApartment(filter, Pageable.unpaged());

        assertThat(result.getContent()).hasSize(1);
    }
}