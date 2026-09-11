package com.search_service.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchBoolPrefixQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.search_service.documents.ComplexDocument;
import com.search_service.dto.out.SearchSuggestionDTO;
import com.search_service.entity.ResidentialComplex;
import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import com.search_service.mapper.ComplexDocumentMapper;
import com.search_service.util.KeyboardLayoutConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Сервис поиска и индексации ЖК в Elasticsearch.
 * Отвечает только за ES: индексацию, удаление, поиск, подсказки.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ComplexSearchService {

    private static final String ANALYZER = "fuzzy_analyzer";
    private static final String INDEX_NAME = "complexes";
    private static final List<String> SEARCH_FIELDS =
            List.of("location", "district", "name", "developer", "metroStation");

    private final ElasticsearchClient esClient;
    private final ComplexDocumentMapper documentMapper;

    /** Индексирует один ЖК. */
    public void indexComplex(ResidentialComplex complex) {
        try {
            ComplexDocument document = documentMapper.toDocument(complex);
            esClient.index(i -> i
                    .index(INDEX_NAME)
                    .id(String.valueOf(document.getId()))
                    .document(document)
            );
            log.info("Жилой комплекс {} загружен в ES.", document.getName());
        } catch (Exception exception) {
            log.error("Ошибка сохранения/индексации ЖК: {}", complex.getName(), exception);
            throw new GeneralFormatException(TypeError.ELASTIC_ERROR);
        }
    }

    /** Удаляет ЖК из индекса. */
    public void deleteComplex(Long id) {
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

    /** Полная переиндексация списка ЖК + refresh индекса. */
    public void reindexAll(List<ResidentialComplex> complexes) {
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
        refreshIndex();
        log.info("Всего переиндексировано {} ЖК. Успешно: {}; С ошибкой: {}",
                complexes.size(), successCount, errorCount);
    }

    /** Обновляет индекс, чтобы документы сразу были доступны для поиска. */
    private void refreshIndex() {
        try {
            esClient.indices().refresh(r -> r.index(INDEX_NAME));
        } catch (Exception exception) {
            log.warn("Не удалось обновить индекс Elasticsearch после реиндексации: {}", exception.toString());
        }
    }

    /** Нечёткий поиск с поддержкой раскладки и опечаток. */
    public List<ComplexDocument> fuzzySearch(String query, int from, int size) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        String q = query.trim();
        List<String> queryVariants = KeyboardLayoutConverter.getSearchVariants(q);
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();

        for (String field : SEARCH_FIELDS) {
            for (String queryVariant : queryVariants) {
                MatchQuery matchQuery = new MatchQuery.Builder()
                        .field(field)
                        .query(queryVariant)
                        .fuzziness("AUTO")
                        .analyzer(ANALYZER)
                        .maxExpansions(50)
                        .build();
                boolBuilder.should(matchQuery._toQuery());

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

    /** Подсказки по всем сущностям, найденным через поиск. */
    public List<SearchSuggestionDTO> suggest(String query, int limit) {
        if (query == null || query.trim().length() < 2) {
            return List.of();
        }
        List<ComplexDocument> docs = fuzzySearch(query, 0, Math.max(limit * 3, 20));
        LinkedHashMap<String, SearchSuggestionDTO> suggestions = new LinkedHashMap<>();

        for (ComplexDocument doc : docs) {
            if (doc == null) continue;
            addIfNotNull(suggestions, doc.getLocationId(), doc.getLocation(), "Локация", query);
            addIfNotNull(suggestions, doc.getDistrictId(), doc.getDistrict(), "Район", query);
            addIfNotNull(suggestions, doc.getDeveloperId(), doc.getDeveloper(), "Застройщик", query);
            addIfNotNull(suggestions, doc.getId(), doc.getName(), "ЖК", query);
            addMetroSuggestions(suggestions, doc.getMetroStationId(), doc.getMetroStation(), query);
            if (suggestions.size() >= limit) break;
        }
        return new ArrayList<>(suggestions.values()).stream().limit(limit).toList();
    }

    private void addIfNotNull(LinkedHashMap<String, SearchSuggestionDTO> map,
                              Long id, String text, String type, String query) {
        if (id == null || text == null || text.isBlank()) return;
        if (matches(text, query)) {
            map.putIfAbsent(type + "|" + text, SearchSuggestionDTO.builder()
                    .entityId(id).text(text).entityType(type).build());
        }
    }

    private void addMetroSuggestions(LinkedHashMap<String, SearchSuggestionDTO> map,
                                     List<Long> ids, List<String> names, String query) {
        if (ids == null || names == null || names.isEmpty()) return;
        for (int i = 0; i < ids.size() && i < names.size(); i++) {
            if (matches(names.get(i), query)) {
                map.putIfAbsent("Метро|" + names.get(i), SearchSuggestionDTO.builder()
                        .entityId(ids.get(i)).text(names.get(i)).entityType("Метро").build());
            }
        }
    }

    /** Мягкое сравнение строки с учётом раскладки и порядка символов. */
    private boolean matches(String text, String query) {
        if (text == null || query == null) return false;
        String normalized = text.toLowerCase();
        List<String> variants = KeyboardLayoutConverter.getSearchVariants(query.toLowerCase());
        for (String variant : variants) {
            if (normalized.contains(variant)) return true;
        }
        for (String variant : variants) {
            if (variant.isEmpty()) continue;
            List<Integer> positions = new ArrayList<>();
            for (int i = 0; i < variant.length(); i++) {
                positions.add(normalized.indexOf(variant.charAt(i)));
            }
            positions.removeIf(n -> n == -1);
            if (positions.size() < variant.length() * 0.5) continue;
            List<Integer> uniquePositions = positions.stream().distinct().toList();
            if (!isSorted(uniquePositions)) continue;
            if (uniquePositions.size() >= variant.length() - 1
                    || uniquePositions.size() >= normalized.length() * 0.5) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSorted(List<Integer> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) > list.get(i + 1)) return false;
        }
        return true;
    }
}