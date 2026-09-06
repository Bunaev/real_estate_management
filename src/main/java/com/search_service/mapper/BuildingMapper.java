package com.search_service.mapper;

import com.search_service.dto.in.BuildingDTO;
import com.search_service.dto.out.BuildingInfoDTO;
import com.search_service.dto.out.BuildingShortDTO;
import com.search_service.dto.out.ResidentialComplexEditDTO;
import com.search_service.entity.Building;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = EntranceMapper.class)
public interface BuildingMapper {

    // === ДЛЯ СОЗДАНИЯ/ОБНОВЛЕНИЯ ===
    @Mapping(target = "residentialComplex", ignore = true)
    Building toEntity(BuildingDTO dto);

    List<Building> toEntityList(List<BuildingDTO> dtos);

    // === ДЛЯ ВЫВОДА В СПИСОК (BuildingShortDTO) ===
    @Mapping(target = "entrances", source = "entrances")
    BuildingShortDTO toShortDto(Building building);

    List<BuildingShortDTO> toShortDtoList(List<Building> buildings);

    // === ДЛЯ РЕДАКТИРОВАНИЯ (BuildingEditDTO) ===
    @Mapping(target = "entrances", source = "entrances")
    ResidentialComplexEditDTO.BuildingEditDTO toEditDto(Building building);

    List<ResidentialComplexEditDTO.BuildingEditDTO> toEditDtoList(List<Building> buildings);

    // === ДЛЯ ИНФОРМАЦИИ О КОРПУСЕ В КВАРТИРЕ ===
    BuildingInfoDTO toInfoDto(Building building);
}