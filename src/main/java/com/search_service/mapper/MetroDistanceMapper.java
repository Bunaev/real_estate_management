package com.search_service.mapper;

import com.search_service.dto.in.MetroDistanceDTO;
import com.search_service.dto.out.MetroDistanceOutDTO;
import com.search_service.dto.out.ResidentialComplexEditDTO;
import com.search_service.entity.ComplexMetroDistance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MetroDistanceMapper {

    // === ДЛЯ СОЗДАНИЯ/ОБНОВЛЕНИЯ ===
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "residentialComplex", ignore = true)
    @Mapping(target = "metroStation", ignore = true)
    ComplexMetroDistance toEntity(MetroDistanceDTO dto);

    List<ComplexMetroDistance> toEntityList(List<MetroDistanceDTO> dtos);

    // === ДЛЯ ВЫВОДА ===
    @Mapping(target = "stationName", source = "metroStation.name")
    MetroDistanceOutDTO toOutDto(ComplexMetroDistance metroDistance);

    List<MetroDistanceOutDTO> toOutDtoList(List<ComplexMetroDistance> metroDistances);

    // === ДЛЯ РЕДАКТИРОВАНИЯ ===
    @Mapping(target = "metroStationId", source = "metroStation.id")
    @Mapping(target = "stationName", source = "metroStation.name")
    ResidentialComplexEditDTO.MetroDistanceEditDTO toEditDto(ComplexMetroDistance metroDistance);

    List<ResidentialComplexEditDTO.MetroDistanceEditDTO> toEditDtoList(List<ComplexMetroDistance> metroDistances);
}