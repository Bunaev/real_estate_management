package com.search_service.model.entityes.dto;

import com.search_service.model.entityes.Building;
import com.search_service.model.entityes.dto.in.BuildingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BuildingMapper {

    @Mapping(target = "residentialComplex", ignore = true)
    @Mapping(target = "entrances", ignore = true)
    Building toEntity(BuildingDTO dto);
}
