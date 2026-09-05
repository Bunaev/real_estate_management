package com.search_service.mapper;

import com.search_service.dto.in.BuildingDTO;
import com.search_service.dto.out.BuildingShortDTO;
import com.search_service.entity.Building;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = EntranceMapper.class)
public interface BuildingMapper {

    @Mapping(target = "residentialComplex", ignore = true)
    @Mapping(target = "entrances", ignore = true)
    Building toEntity(BuildingDTO dto);

    List<BuildingShortDTO> toListDTO(List<Building> buildings);
}