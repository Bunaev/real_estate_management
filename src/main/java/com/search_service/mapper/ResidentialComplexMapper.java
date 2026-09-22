package com.search_service.mapper;

import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.*;
import com.search_service.entity.ResidentialComplex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BuildingMapper.class, MetroDistanceMapper.class})
public interface ResidentialComplexMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "developer", ignore = true)
    @Mapping(target = "metroDistances", ignore = true)
    @Mapping(target = "buildings", ignore = true)
    ResidentialComplex toEntity(ResidentialComplexDTO dto);

    @Mapping(target = "developer", source = "developer.name")
    @Mapping(target = "fullAddress", expression = "java(buildFullAddress(complex))")
    ResidentialComplexShortDTO toShortDto(ResidentialComplex complex);

    @Mapping(target = "developer", source = "developer.name")
    @Mapping(target = "district", source = "district.name")
    @Mapping(target = "location", source = "district.location.name")
    @Mapping(target = "buildings", source = "buildings")
    @Mapping(target = "metroDistances", source = "metroDistances")
    @Mapping(target = "countApartment", expression = "java(calculateApartmentCount(complex))")
    ResidentialComplexDetailDTO toDetailDto(ResidentialComplex complex);

    @Mapping(target = "locationId", source = "district.location.id")
    @Mapping(target = "districtId", source = "district.id")
    @Mapping(target = "developerId", source = "developer.id")
    @Mapping(target = "locationName", source = "district.location.name")
    @Mapping(target = "districtName", source = "district.name")
    @Mapping(target = "developerName", source = "developer.name")
    @Mapping(target = "metroDistances", source = "metroDistances")
    @Mapping(target = "buildings", source = "buildings")
    @Mapping(target = "countBuildings", expression = "java(complex.getBuildings() != null ? complex.getBuildings().size() : 0)")
    @Mapping(target = "countEntrance", expression = "java(calculateEntranceCount(complex))")
    @Mapping(target = "countApartment", expression = "java(calculateApartmentCount(complex))")
    ResidentialComplexEditDTO toEditDto(ResidentialComplex complex);

    @Mapping(target = "locationName", source = "district.location.name")
    @Mapping(target = "districtName", source = "district.name")
    @Mapping(target = "developerName", source = "developer.name")
    @Mapping(target = "countBuildings", expression = "java(complex.getBuildings() != null ? complex.getBuildings().size() : 0)")
    @Mapping(target = "countEntrance", expression = "java(calculateEntranceCount(complex))")
    @Mapping(target = "countApartment", expression = "java(calculateApartmentCount(complex))")
    @Mapping(target = "metroDistances", source = "metroDistances")
    ComplexCardOutDTO toCardOutDto(ResidentialComplex complex);

    default int calculateEntranceCount(ResidentialComplex complex) {
        if (complex == null || complex.getBuildings() == null) return 0;
        return (int) complex.getBuildings().stream()
                .filter(b -> b.getEntrances() != null)
                .mapToLong(b -> b.getEntrances().size())
                .sum();
    }

    default int calculateApartmentCount(ResidentialComplex complex) {
        if (complex == null || complex.getBuildings() == null) return 0;
        return (int) complex.getBuildings().stream()
                .filter(b -> b.getEntrances() != null)
                .flatMap(b -> b.getEntrances().stream())
                .filter(e -> e.getApartments() != null)
                .mapToLong(e -> e.getApartments().size())
                .sum();
    }

    default String buildFullAddress(ResidentialComplex complex) {
        if (complex == null) return "Не указан";
        String location = complex.getDistrict() != null && complex.getDistrict().getLocation() != null
                ? complex.getDistrict().getLocation().getName()
                : "Не указана";
        String district = complex.getDistrict() != null
                ? complex.getDistrict().getName()
                : "Не указан";
        return location + ", " + district + " район, " + complex.getAddress();
    }
}