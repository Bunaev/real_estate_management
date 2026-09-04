package com.search_service.model.entityes.dto;

import com.search_service.model.entityes.Building;
import com.search_service.model.entityes.Entrance;
import com.search_service.model.entityes.ResidentialComplex;
import com.search_service.model.entityes.dto.out.MetroDistanceOutDTO;
import com.search_service.model.entityes.dto.out.ResidentialComplexDetailDTO;
import com.search_service.model.entityes.dto.out.ResidentialComplexOutDTO;
import com.search_service.model.entityes.dto.out.ResidentialComplexShortDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
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

        // Маппим метро в DTO
        List<MetroDistanceOutDTO> metroDTOs = complex.getMetroDistances() != null
                ? complex.getMetroDistances().stream()
                .map(md -> MetroDistanceOutDTO.builder()
                        .stationName(md.getMetroStation().getName())
                        .distance(md.getDistance())
                        .build())
                .toList()
                : List.of();

        return ResidentialComplexOutDTO.builder()
                .id(complex.getId())  // ← добавил ID
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

    default ResidentialComplexShortDTO toShortDto (ResidentialComplex complex) {
        String fullAddress = complex.getDistrict().getLocation().getName() + " ," +
                complex.getDistrict().getName() + " район, " + complex.getAddress();
        return ResidentialComplexShortDTO.builder()
                .id(complex.getId())
                .name(complex.getName())
                .developer(complex.getDeveloper().getName())
                .fullAddress(fullAddress).build();
    }

    default ResidentialComplexDetailDTO toDetailDto(ResidentialComplex complex) {
        if (complex == null) return null;

        return ResidentialComplexDetailDTO.builder()
                .id(complex.getId())
                .name(complex.getName())
                .address(complex.getAddress())
                .developer(complex.getDeveloper() != null ? complex.getDeveloper().getName() : null)
                .district(complex.getDistrict() != null ? complex.getDistrict().getName() : null)
                .location(complex.getDistrict() != null && complex.getDistrict().getLocation() != null
                        ? complex.getDistrict().getLocation().getName() : null)
                .buildings(complex.getBuildings())
                .build();
    }
}