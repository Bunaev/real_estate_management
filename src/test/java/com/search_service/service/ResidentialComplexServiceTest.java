package com.search_service.service;

import com.search_service.documents.ComplexDocument;
import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.ResidentialComplexEditDTO;
import com.search_service.dto.out.ResidentialComplexShortDTO;
import com.search_service.dto.out.SearchSuggestionDTO;
import com.search_service.entity.Developer;
import com.search_service.entity.District;
import com.search_service.entity.Location;
import com.search_service.entity.ResidentialComplex;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.ResidentialComplexMapper;
import com.search_service.repository.DeveloperRepo;
import com.search_service.repository.DistrictRepo;
import com.search_service.repository.ResidentialComplexRepo;
import com.search_service.specification.SpecificationBuilder;
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
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResidentialComplexServiceTest {

    @Mock private ResidentialComplexRepo complexRepo;
    @Mock private DistrictRepo districtRepo;
    @Mock private DeveloperRepo developerRepo;
    @Mock private ResidentialComplexMapper mapper;
    @Mock private SpecificationBuilder specificationBuilder;
    @Mock private FileStorageService fileStorageService;
    @Mock private ComplexSearchService complexSearchService;
    @Mock private ResidentialComplexRelationService relationService;

    @InjectMocks
    private ResidentialComplexService service;

    private ResidentialComplex complex(Long id) {
        return ResidentialComplex.builder()
                .id(id)
                .name("Vertical Московская")
                .address("Московский пр.")
                .district(District.builder().id(1L).name("Московский")
                        .location(Location.builder().id(1L).name("Санкт-Петербург").build()).build())
                .developer(Developer.builder().id(1L).name("Setl Group").build())
                .build();
    }

    @Test
    void create_shouldSaveIndexAndReturnDto() {
        ResidentialComplexDTO dto = ResidentialComplexDTO.builder()
                .name("Vertical Московская").districtId(1L).developerId(1L).build();
        ResidentialComplex unsaved = ResidentialComplex.builder().id(1L).name("Vertical Московская").build();
        ResidentialComplex saved = complex(1L);
        ResidentialComplexShortDTO shortDto = ResidentialComplexShortDTO.builder().id(1L).name("Vertical Московская").build();

        when(mapper.toEntity(any(ResidentialComplexDTO.class))).thenReturn(unsaved);
        when(districtRepo.findById(1L)).thenReturn(Optional.of(saved.getDistrict()));
        when(developerRepo.findById(1L)).thenReturn(Optional.of(saved.getDeveloper()));
        when(complexRepo.save(any(ResidentialComplex.class))).thenReturn(saved);
        when(mapper.toShortDto(any(ResidentialComplex.class))).thenReturn(shortDto);

        ResidentialComplexShortDTO result = service.create(dto, null);

        assertThat(result.getId()).isEqualTo(1L);
        verify(relationService).applyRelations(any(ResidentialComplex.class), any(ResidentialComplexDTO.class));
        verify(complexSearchService).indexComplex(saved);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(complexRepo.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("ЖК");
    }

    @Test
    void delete_shouldDeleteFromSearchRepositoryAndFiles() {
        when(complexRepo.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(complexSearchService).deleteComplex(1L);
        verify(complexRepo).deleteById(1L);
        verify(fileStorageService).deleteComplexFiles(1L);
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        when(complexRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void findForEdit_shouldReturnDto() {
        ResidentialComplex saved = complex(1L);
        when(complexRepo.findById(1L)).thenReturn(Optional.of(saved));
        when(mapper.toEditDto(saved)).thenReturn(new ResidentialComplexEditDTO());

        assertThat(service.findForEdit(1L)).isNotNull();
    }

    @Test
    void reindexAllComplex_shouldDelegateToSearchService() {
        List<ResidentialComplex> complexes = List.of(complex(1L));
        when(complexRepo.findAll()).thenReturn(complexes);

        service.reindexAllComplex();

        verify(complexSearchService).reindexAll(complexes);
    }

    @Test
    void fuzzySearch_shouldDelegateToSearchService() {
        List<ComplexDocument> docs = List.of(ComplexDocument.builder().id(1L).build());
        when(complexSearchService.fuzzySearch("моск", 0, 10)).thenReturn(docs);

        assertThat(service.fuzzySearch("моск", 0, 10)).isEqualTo(docs);
    }

    @Test
    void suggest_shouldDelegateToSearchService() {
        List<SearchSuggestionDTO> suggestions = List.of(
                SearchSuggestionDTO.builder().entityId(1L).text("Vertical").entityType("ЖК").build()
        );
        when(complexSearchService.suggest("моск", 4)).thenReturn(suggestions);

        assertThat(service.suggest("моск", 4)).isEqualTo(suggestions);
    }
}