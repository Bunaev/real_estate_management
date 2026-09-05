package com.search_service.mapper;

import com.search_service.dto.out.*;
import com.search_service.entity.*;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = BuildingMapper.class)
public interface ResidentialComplexOutMapper {

    default ResidentialComplexOutDTO toDto(ResidentialComplex complex) {
        if (complex == null) return null;

        List<Building> buildings = complex.getBuildings();
        List<Entrance> entrances = buildings != null
                ? buildings.stream()
                .filter(b -> b.getEntrances() != null)
                .flatMap(b -> b.getEntrances().stream())
                .toList()
                : List.of();

        long countApartment = entrances.stream()
                .filter(e -> e.getApartments() != null)
                .mapToLong(e -> e.getApartments().size())
                .sum();

        List<MetroDistanceOutDTO> metroDTOs = complex.getMetroDistances() != null
                ? complex.getMetroDistances().stream()
                .map(md -> MetroDistanceOutDTO.builder()
                        .stationName(md.getMetroStation().getName())
                        .distance(md.getDistance())
                        .build())
                .toList()
                : List.of();

        return ResidentialComplexOutDTO.builder()
                .id(complex.getId())
                .name(complex.getName())
                .developer(complex.getDeveloper() != null
                        ? complex.getDeveloper().getName()
                        : "Не указан")
                .location(complex.getDistrict() != null
                        && complex.getDistrict().getLocation() != null
                        ? complex.getDistrict().getLocation().getName()
                        : "Не указана")
                .district(complex.getDistrict() != null
                        ? complex.getDistrict().getName()
                        : "Не указан")
                .address(complex.getAddress())
                .metroDistances(metroDTOs)
                .countBuildings(buildings != null ? buildings.size() : 0)
                .countEntrance(entrances.size())
                .countApartment((int) countApartment)
                .build();
    }

    default ResidentialComplexShortDTO toShortDto(ResidentialComplex complex) {
        if (complex == null) return null;

        String locationName = complex.getDistrict() != null && complex.getDistrict().getLocation() != null
                ? complex.getDistrict().getLocation().getName()
                : "Не указана";

        String districtName = complex.getDistrict() != null
                ? complex.getDistrict().getName()
                : "Не указан";

        String developerName = complex.getDeveloper() != null
                ? complex.getDeveloper().getName()
                : "Не указан";

        String fullAddress = locationName + ", " + districtName + " район, " + complex.getAddress();

        return ResidentialComplexShortDTO.builder()
                .id(complex.getId())
                .name(complex.getName())
                .developer(developerName)
                .fullAddress(fullAddress)
                .build();
    }

    default ResidentialComplexDetailDTO toDetailDto(ResidentialComplex complex, BuildingMapper buildingMapper) {
        if (complex == null) return null;

        List<BuildingShortDTO> buildingDTOs = buildingMapper.toListDTO(complex.getBuildings());

        return ResidentialComplexDetailDTO.builder()
                .id(complex.getId())
                .name(complex.getName())
                .address(complex.getAddress())
                .developer(complex.getDeveloper() != null ? complex.getDeveloper().getName() : null)
                .district(complex.getDistrict() != null ? complex.getDistrict().getName() : null)
                .location(complex.getDistrict() != null && complex.getDistrict().getLocation() != null
                        ? complex.getDistrict().getLocation().getName() : null)
                .buildings(buildingDTOs)
                .build();
    }
}