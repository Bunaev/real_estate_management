package com.search_service.mapper;

import com.search_service.documents.ComplexDocument;
import com.search_service.entity.ResidentialComplex;
import org.springframework.stereotype.Component;

@Component
public class ComplexDocumentMapper {

    public ComplexDocument toDocument(ResidentialComplex complex) {
        if (complex == null) {
            return null;
        }
        var metroDistances = complex.getMetroDistances() == null ? java.util.List.<com.search_service.entity.ComplexMetroDistance>of() : complex.getMetroDistances();
        return ComplexDocument.builder()
                .id(complex.getId())
                .locationId(complex.getDistrict().getLocation().getId())
                .districtId(complex.getDistrict().getId())
                .developerId(complex.getDeveloper().getId())
                .name(complex.getName())
                .location(complex.getDistrict().getLocation().getName())
                .district(complex.getDistrict().getName())
                .developer(complex.getDeveloper().getName())
                .metroStation(metroDistances.stream()
                        .map(m -> m.getMetroStation().getName()).toList())
                .metroStationId(metroDistances.stream().map(metro -> metro.getMetroStation().getId()).toList())
                .build();
    }
}
