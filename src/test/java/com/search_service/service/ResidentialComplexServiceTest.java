package com.search_service.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import com.search_service.documents.ComplexDocument;
import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.ResidentialComplexEditDTO;
import com.search_service.dto.out.ResidentialComplexShortDTO;
import com.search_service.dto.out.SearchSuggestionDTO;
import com.search_service.entity.*;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.BuildingMapper;
import com.search_service.mapper.ComplexDocumentMapper;
import com.search_service.mapper.ResidentialComplexMapper;
import com.search_service.repository.DeveloperRepo;
import com.search_service.repository.DistrictRepo;
import com.search_service.repository.MetroStationRepo;
import com.search_service.repository.ResidentialComplexRepo;
import com.search_service.specification.SpecificationBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.function.Function;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResidentialComplexServiceTest {

    @Mock private ResidentialComplexRepo complexRepo;
    @Mock private DistrictRepo districtRepo;
    @Mock private DeveloperRepo developerRepo;
    @Mock private MetroStationRepo metroRepo;
    @Mock private ResidentialComplexMapper mapper;
    @Mock private BuildingMapper buildingMapper;
    @Mock private SpecificationBuilder specificationBuilder;
    @Mock private ElasticsearchClient esClient;
    @Mock private ComplexDocumentMapper documentMapper;

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
    void create_shouldSaveAndIndexComplex() throws Exception {
        ResidentialComplexDTO dto = ResidentialComplexDTO.builder()
                .name("Vertical Московская")
                .districtId(1L)
                .developerId(1L)
                .build();
        ResidentialComplex unsaved = ResidentialComplex.builder().id(1L).name("Vertical Московская").build();
        ResidentialComplex saved = complex(1L);
        ResidentialComplexShortDTO shortDto = ResidentialComplexShortDTO.builder()
                .id(1L).name("Vertical Московская").developer("Setl Group").build();

        when(mapper.toEntity(any(ResidentialComplexDTO.class))).thenReturn(unsaved);
        when(districtRepo.findById(1L)).thenReturn(Optional.of(saved.getDistrict()));
        when(developerRepo.findById(1L)).thenReturn(Optional.of(saved.getDeveloper()));
        when(complexRepo.save(any(ResidentialComplex.class))).thenReturn(saved);
        when(documentMapper.toDocument(any(ResidentialComplex.class)))
                .thenReturn(ComplexDocument.builder().id(1L).name("Vertical Московская").build());
        when(esClient.index(any(Function.class))).thenReturn(mock(IndexResponse.class));
        when(mapper.toShortDto(any(ResidentialComplex.class))).thenReturn(shortDto);

        ResidentialComplexShortDTO result = service.create(dto);

        assertThat(result.getId()).isEqualTo(1L);
        verify(esClient).index(any(Function.class));
    }

    @Test
    void delete_shouldThrow_whenNotFound() {
        when(complexRepo.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("ЖК");
    }

    @Test
    void delete_shouldDeleteFromElasticsearch() throws Exception {
        when(complexRepo.existsById(1L)).thenReturn(true);
        when(esClient.delete(any(Function.class))).thenReturn(mock(DeleteResponse.class));

        service.delete(1L);

        verify(esClient).delete(any(Function.class));
    }

    @Test
    void findById_shouldThrow_whenNotFound() {
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
    void reindexAllComplex_shouldIndexAllComplexes() throws Exception {
        ResidentialComplex saved = complex(1L);
        co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient indices =
                mock(co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient.class);
        when(complexRepo.findAll()).thenReturn(List.of(saved));
        when(esClient.indices()).thenReturn(indices);
        when(indices.refresh(any(Function.class)))
                .thenReturn(mock(co.elastic.clients.elasticsearch.indices.RefreshResponse.class));
        when(esClient.index(any(Function.class))).thenReturn(mock(IndexResponse.class));

        service.reindexAllComplex();

        verify(esClient, times(1)).index(any(Function.class));
    }

    @Test
    void fuzzySearch_shouldReturnHits() throws Exception {
        ComplexDocument doc = ComplexDocument.builder()
                .id(1L).name("Vertical Московская").district("Московский")
                .location("Санкт-Петербург").developer("Setl Group")
                .build();
        SearchResponse<ComplexDocument> response = mock(SearchResponse.class);
        HitsMetadata<ComplexDocument> metadata = mock(HitsMetadata.class);
        Hit<ComplexDocument> hit = mock(Hit.class);
        when(esClient.search(any(SearchRequest.class), eq(ComplexDocument.class))).thenReturn(response);
        when(response.hits()).thenReturn(metadata);
        when(metadata.hits()).thenReturn(List.of(hit));
        when(hit.source()).thenReturn(doc);

        List<ComplexDocument> result = service.fuzzySearch("моск", 0, 10);

        assertThat(result).containsExactly(doc);
    }

    @Test
    void suggest_shouldReturnDeduplicatedSuggestions() throws Exception {
        ComplexDocument doc = ComplexDocument.builder()
                .id(1L).name("Vertical Московская").district("Московский")
                .location("Санкт-Петербург").developer("Setl Group")
                .locationId(1L).districtId(2L).developerId(3L)
                .build();
        SearchResponse<ComplexDocument> response = mock(SearchResponse.class);
        HitsMetadata<ComplexDocument> metadata = mock(HitsMetadata.class);
        Hit<ComplexDocument> hit = mock(Hit.class);
        when(esClient.search(any(SearchRequest.class), eq(ComplexDocument.class))).thenReturn(response);
        when(response.hits()).thenReturn(metadata);
        when(metadata.hits()).thenReturn(List.of(hit));
        when(hit.source()).thenReturn(doc);

        List<SearchSuggestionDTO> suggestions = service.suggest("моск", 4);

        assertThat(suggestions).isNotEmpty();
        assertThat(suggestions).extracting(SearchSuggestionDTO::getEntityType)
                .contains("Локация", "Район", "Застройщик", "ЖК");
    }
}
