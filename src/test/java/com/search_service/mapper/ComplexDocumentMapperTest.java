package com.search_service.mapper;

import com.search_service.documents.ComplexDocument;
import com.search_service.entity.Developer;
import com.search_service.entity.District;
import com.search_service.entity.Location;
import com.search_service.entity.ResidentialComplex;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ComplexDocumentMapperTest {

    private final ComplexDocumentMapper mapper = new ComplexDocumentMapper();

    @Test
    void shouldMapComplexToDocument() {
        ResidentialComplex complex = ResidentialComplex.builder()
                .id(42L)
                .name("ЖК Пример")
                .district(District.builder().id(7L).name("Центральный")
                        .location(Location.builder().id(1L).name("Санкт-Петербург").build()).build())
                .developer(Developer.builder().id(3L).name("Застройщик").build())
                .build();

        ComplexDocument document = mapper.toDocument(complex);

        assertThat(document.getId()).isEqualTo(42L);
        assertThat(document.getName()).isEqualTo("ЖК Пример");
        assertThat(document.getDistrictId()).isEqualTo(7L);
        assertThat(document.getLocationId()).isEqualTo(1L);
        assertThat(document.getDeveloperId()).isEqualTo(3L);
        assertThat(document.getDistrict()).isEqualTo("Центральный");
        assertThat(document.getLocation()).isEqualTo("Санкт-Петербург");
        assertThat(document.getDeveloper()).isEqualTo("Застройщик");
    }

    @Test
    void shouldReturnNullForNullComplex() {
        assertThat(mapper.toDocument(null)).isNull();
    }
}
