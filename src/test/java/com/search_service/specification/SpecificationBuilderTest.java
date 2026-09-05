package com.search_service.specification;

import com.search_service.dto.in.FilterDTO;
import com.search_service.entity.*;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class SpecificationBuilderTest {

    private final SpecificationBuilder specificationBuilder = new SpecificationBuilder();

    @Test
    void build_shouldReturnNonNullSpecificationForEmptyFilter() {
        var spec = specificationBuilder.build(FilterDTO.builder().build());
        assertThat(spec).isNotNull();
    }

    @Test
    void build_withResidentialComplexId_shouldCreateNonNullSpec() {
        FilterDTO filter = FilterDTO.builder().residentialComplexId(1L).build();
        var spec = specificationBuilder.build(filter);
        assertThat(spec).isNotNull();
    }

    @Test
    void build_withMultipleFilters_shouldCreateNonNullSpec() {
        FilterDTO filter = FilterDTO.builder()
                .residentialComplexId(1L)
                .floorFrom(3)
                .floorTo(10)
                .areaFrom(30.0)
                .areaTo(80.0)
                .hasBalcony(true)
                .types(List.of(ApartmentType.ONE_ROOM, ApartmentType.TWO_ROOM))
                .build();

        var spec = specificationBuilder.build(filter);
        assertThat(spec).isNotNull();
    }

    @Test
    void build_withAllNulls_shouldReturnConjunction() {
        FilterDTO filter = FilterDTO.builder().build();
        var spec = specificationBuilder.build(filter);
        assertThat(spec).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    void build_withIds_shouldCreateNonNullPredicate() {
        FilterDTO filter = FilterDTO.builder()
                .locationId(1L)
                .districtId(1L)
                .developerId(1L)
                .buildingId(1L)
                .entranceId(1L)
                .metroStationIds(List.of(1L, 2L))
                .build();

        var spec = specificationBuilder.build(filter);
        assertThat(spec).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    void build_withSpecificStatus_shouldNotBeNull() {
        FilterDTO filter = FilterDTO.builder()
                .status(Status.AVAILABLE)
                .bathroomType(BathroomType.COMBINED)
                .priceFrom(1000000.0)
                .priceTo(10000000.0)
                .build();

        var spec = specificationBuilder.build(filter);
        assertThat(spec).isNotNull();
    }
}