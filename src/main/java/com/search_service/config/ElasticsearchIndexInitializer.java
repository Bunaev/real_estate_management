package com.search_service.config;

import com.search_service.documents.ComplexDocument;
import com.search_service.service.ResidentialComplexService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * Creates the Elasticsearch index automatically when it is absent.
 *
 * <p>The initial full reindex is performed only once: when the index has just been
 * created or exists but is empty. It is not run on every page load or on every
 * application start if the index already contains documents.</p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchIndexInitializer implements ApplicationRunner {

    private final ElasticsearchOperations operations;
    private final ResidentialComplexService complexService;

    @Override
    public void run(ApplicationArguments args) {
        IndexOperations indexOps = operations.indexOps(ComplexDocument.class);

        if (indexOps.exists()) {
            long documentCount = operations.count(Query.findAll(), ComplexDocument.class);
            if (documentCount > 0) {
                log.info("Elasticsearch index 'complexes' already exists and contains {} documents.", documentCount);
                return;
            }
            log.warn("Elasticsearch index 'complexes' exists but is empty. Reindexing from DB...");
            complexService.reindexAllComplex();
            return;
        }

        log.info("Elasticsearch index 'complexes' not found. Creating it with @Setting and @Field mapping...");
        boolean created = indexOps.createWithMapping();
        if (!created) {
            throw new IllegalStateException("Failed to create Elasticsearch index 'complexes'");
        }
        log.info("Elasticsearch index 'complexes' created. Running initial reindex...");
        complexService.reindexAllComplex();
    }
}
