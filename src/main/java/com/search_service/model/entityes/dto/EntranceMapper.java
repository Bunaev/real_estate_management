package com.search_service.model.entityes.dto;

import com.search_service.model.entityes.Entrance;
import com.search_service.model.entityes.dto.in.EntranceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntranceMapper {

    @Mapping(target = "apartments", ignore = true)
    @Mapping(target = "building", ignore = true)
    Entrance toEntity(EntranceDTO dto);
}
