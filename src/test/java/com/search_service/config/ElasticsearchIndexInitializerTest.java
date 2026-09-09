package com.search_service.config;

import com.search_service.documents.ComplexDocument;
import com.search_service.service.ResidentialComplexService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.query.Query;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ElasticsearchIndexInitializerTest {

    @Mock
    private ElasticsearchOperations operations;
    @Mock
    private ResidentialComplexService complexService;
    @Mock
    private IndexOperations indexOperations;

    @InjectMocks
    private ElasticsearchIndexInitializer initializer;

    @Test
    void shouldNotReindexWhenIndexExistsAndHasDocuments() throws Exception {
        when(operations.indexOps(ComplexDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(true);
        when(operations.count(any(Query.class), eq(ComplexDocument.class))).thenReturn(5L);

        initializer.run(null);

        verify(complexService, never()).reindexAllComplex();
        verify(indexOperations, never()).createWithMapping();
    }

    @Test
    void shouldReindexWhenIndexExistsButIsEmpty() throws Exception {
        when(operations.indexOps(ComplexDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(true);
        when(operations.count(any(Query.class), eq(ComplexDocument.class))).thenReturn(0L);

        initializer.run(null);

        verify(complexService).reindexAllComplex();
    }

    @Test
    void shouldCreateIndexAndReindexWhenIndexIsAbsent() throws Exception {
        when(operations.indexOps(ComplexDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(false);
        when(indexOperations.createWithMapping()).thenReturn(true);

        initializer.run(null);

        verify(indexOperations).createWithMapping();
        verify(complexService).reindexAllComplex();
    }

    @Test
    void shouldThrowWhenIndexCreationFails() throws Exception {
        when(operations.indexOps(ComplexDocument.class)).thenReturn(indexOperations);
        when(indexOperations.exists()).thenReturn(false);
        when(indexOperations.createWithMapping()).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalStateException.class,
                () -> initializer.run(null)
        );
    }
}
