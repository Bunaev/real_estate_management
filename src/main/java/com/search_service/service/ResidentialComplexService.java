package com.search_service.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchBoolPrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.search_service.documents.ComplexDocument;
import com.search_service.dto.in.*;
import com.search_service.dto.out.*;
import com.search_service.entity.*;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import com.search_service.mapper.*;
import com.search_service.repository.*;
import com.search_service.specification.SpecificationBuilder;
import com.search_service.util.KeyboardLayoutConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
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
    private final ElasticsearchClient esClient;
    private final ComplexDocumentMapper documentMapper;
    private static final String ANALYZER = "fuzzy_analyzer";
    private static final String INDEX_NAME = "complexes";


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
        indexComplex(saved);
        return mapper.toShortDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ResidentialComplexOutDTO> findAllLightweight(FilterDTO filter, Pageable page) {
        Specification<ResidentialComplex> specification = specificationBuilder.buildComplexes(filter);

        Page<ResidentialComplex> complexPage = complexRepo.findAll(specification, page);
        List<ResidentialComplexOutDTO> dtoList = complexPage.getContent().stream()
                .map(mapper::toOutDto)
                .collect(Collectors.toList());

        List<Long> ids = dtoList.stream()
                .map(ResidentialComplexOutDTO::getId)
                .collect(Collectors.toList());

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

            dtoList.forEach(dto ->
                    dto.setMetroDistances(metroMap.getOrDefault(dto.getId(), List.of()))
            );
        }
        return new PageImpl<>(dtoList, complexPage.getPageable(), complexPage.getTotalElements());
    }

    @Transactional
    public void delete(Long id) {
        if (!complexRepo.existsById(id)) {
            throw new EntityNotFoundException("ЖК", id);
        }
        try {
            esClient.delete(d -> d
                    .index(INDEX_NAME)
                    .id(String.valueOf(id))
            );
            log.info("ЖК с ID {} удален из ES", id);
        } catch (Exception e) {
            log.error("Ошибка удаления из ES: {}", e.getMessage());
        }
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
        indexComplex(saved);
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

     private void indexComplex(ResidentialComplex complex) {
        try {
            ComplexDocument document = documentMapper.toDocument(complex);
            esClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(String.valueOf(document.getId()))
                    .document(document)
            );
            log.info("Жилой комплекс {} загружен в ES.", document.getName());
        } catch (Exception exception) {
            log.info("Ошибка сохранения/индексации ЖК: {}", complex.getName());
            throw new GeneralFormatException(TypeError.ELASTIC_ERROR);
        }
     }

     @Transactional(readOnly = true)
    public void reindexAllComplex() {
        List<ResidentialComplex> complexes = complexRepo.findAll();
        int successCount = 0;
        int errorCount = 0;
        for (ResidentialComplex complex : complexes) {
            try {
                esClient.index(i -> i
                        .index(INDEX_NAME)
                        .id(String.valueOf(complex.getId()))
                        .document(documentMapper.toDocument(complex))
                );
                successCount++;
            } catch (Exception exception) {
                errorCount++;
                log.warn("Ошибка реиндексации ЖК {}: {}", complex.getName(), exception.toString());
            }
        }
        try {
            esClient.indices().refresh(r -> r.index(INDEX_NAME));
        } catch (Exception exception) {
            log.warn("Не удалось обновить индекс Elasticsearch после реиндексации: {}", exception.toString());
        }

        log.info("Всего переиндексировано {} ЖК. Успешно: {}; С ошибкой: {}", complexes.size(), successCount, errorCount);
     }


    public List<ComplexDocument> fuzzySearch(String query, int from, int size) {
        if (query == null || query.trim().length() < 2) return List.of();
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        String q = query.trim();
        List<String> queryVariants = KeyboardLayoutConverter.getSearchVariants(q);

        for (String field : List.of("location", "district", "name", "developer", "metroStation")) {
            for (String queryVariant : queryVariants) {
                MatchQuery mq = new MatchQuery.Builder()
                        .field(field)
                        .query(queryVariant)
                        .fuzziness("AUTO")
                        .analyzer(ANALYZER)
                        .maxExpansions(50)
                        .build();
                boolBuilder.should(mq._toQuery());

                MatchBoolPrefixQuery prefixQuery = new MatchBoolPrefixQuery.Builder()
                        .field(field)
                        .query(queryVariant)
                        .fuzziness("AUTO")
                        .analyzer(ANALYZER)
                        .maxExpansions(50)
                        .build();
                boolBuilder.should(prefixQuery._toQuery());
            }
        }

        SearchRequest request = new SearchRequest.Builder()
                .index(INDEX_NAME)
                .query(b -> b.bool(boolBuilder.minimumShouldMatch("1").build()))
                .from(from)
                .size(size)
                .build();
        try {
            SearchResponse<ComplexDocument> response = esClient.search(request, ComplexDocument.class);
            return response.hits().hits().stream()
                    .map(Hit::source)
                    .toList();
        } catch (IOException e) {
            log.error("ES search failed", e);
            return List.of();
        }
    }

    public List<SearchSuggestionDTO> suggest(String query, int limit) {
        if (query == null || query.trim().length() < 2) return List.of();
        List<ComplexDocument> docs = fuzzySearch(query, 0, Math.max(limit * 3, 20));
        LinkedHashMap<String, SearchSuggestionDTO> suggestions = new LinkedHashMap<>();
        for (ComplexDocument doc : docs) {
            if (doc == null) continue;
            addIfNotNull(suggestions, doc.getLocationId(), doc.getLocation(), "Локация", query);
            addIfNotNull(suggestions, doc.getDistrictId(), doc.getDistrict(), "Район", query);
            addIfNotNull(suggestions, doc.getDeveloperId(), doc.getDeveloper(), "Застройщик", query);
            addIfNotNull(suggestions, doc.getId(), doc.getName(), "ЖК", query);
            addIfNotNullMetroStations(suggestions, doc.getMetroStationId(), doc.getMetroStation(), "Метро", query);
            if (suggestions.size() >= limit) break;
        }
        return new ArrayList<>(suggestions.values()).stream().limit(limit).toList();
    }

    private void addIfNotNull(LinkedHashMap<String, SearchSuggestionDTO> map,
                               Long id, String text, String type, String query) {
        if (id == null || text == null || text.isBlank()) return;
        String key = type + "|" + text;
        if (comparison(text, query)) {
            map.putIfAbsent(key, SearchSuggestionDTO.builder()
                    .entityId(id).text(text).entityType(type).build());
        }
    }

    private boolean comparison(String text, String query) {
        if (text == null || query == null) return false;

        String normalized = text.toLowerCase();
        String q = query.toLowerCase();
        List<String> variants = KeyboardLayoutConverter.getSearchVariants(q);
        for (String variant : variants) {
            if (normalized.contains(variant)) {
                return true;
            }
        }
        for (String variant : variants) {
            if (variant.isEmpty()) continue;
            List<Integer> positions = new ArrayList<>();
            for (int i = 0; i < variant.length(); i++) {
                positions.add(normalized.indexOf(variant.charAt(i)));
            }
            positions.removeIf(n -> n == -1);
            if (positions.size() < variant.length() * 0.5) continue;
            List<Integer> uniquePositions = positions.stream()
                    .distinct().toList();

            if (!isSorted(uniquePositions)) continue;
            if (uniquePositions.size() >= variant.length() - 1) {
                return true;
            }
            if (uniquePositions.size() >= normalized.length() * 0.5) {
                return true;
            }
        }

        return false;
    }

    private static boolean isSorted(List<Integer> list) {
        if (list == null || list.size() <= 1) return true;
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1)) return false;
        }
        return true;
    }

    private void addIfNotNullMetroStations(LinkedHashMap<String, SearchSuggestionDTO> map,
                              List<Long> id, List<String> text, String type, String query) {
        if (id == null || text == null || text.isEmpty()) return;
        for (int i = 0; i < id.size(); i++) {
            if (comparison(text.get(i), query)) {
                map.putIfAbsent(type, SearchSuggestionDTO.builder()
                        .entityId(id.get(i)).text(text.get(i)).entityType(type).build());
            }
        }
    }
}
