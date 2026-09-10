package com.search_service.integration;

import com.search_service.dto.out.SearchSuggestionDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class SearchServiceIntegrationTest {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("search_service")
            .withUsername("postgres")
            .withPassword("postgres");

    @Container
    static final GenericContainer<?> ELASTICSEARCH = new GenericContainer<>("docker.elastic.co/elasticsearch/elasticsearch:8.11.0")
            .withExposedPorts(9200)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "false")
            .withEnv("ES_JAVA_OPTS", "-Xms256m -Xmx256m")
            .waitingFor(Wait.forHttp("/")
                    .forPort(9200)
                    .forStatusCode(200));

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.elasticsearch.uris", () -> "http://" + ELASTICSEARCH.getHost() + ":" + ELASTICSEARCH.getMappedPort(9200));
    }

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void suggest_shouldFindByRussianPrefix() {
        ResponseEntity<List<SearchSuggestionDTO>> response = restTemplate.exchange(
                "/api/complexes/suggest?q=моск&limit=5",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .extracting(SearchSuggestionDTO::getText)
                .anyMatch(text -> text != null && text.toLowerCase().contains("моск"));
    }

    @Test
    void suggest_shouldFindTypoWithWrongKeyboardLayout() {
        ResponseEntity<List<SearchSuggestionDTO>> response = restTemplate.exchange(
                "/api/complexes/suggest?q=vjcrjd&limit=5",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody())
                .extracting(SearchSuggestionDTO::getText)
                .anyMatch(text -> text != null && text.toLowerCase().contains("моск"));
    }
}
