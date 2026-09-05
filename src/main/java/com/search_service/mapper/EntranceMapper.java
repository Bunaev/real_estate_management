package com.search_service.mapper;

import com.search_service.dto.in.EntranceDTO;
import com.search_service.dto.out.EntranceShortDTO;
import com.search_service.entity.Entrance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EntranceMapper {

    @Mapping(target = "apartments", ignore = true)
    @Mapping(target = "building", ignore = true)
    Entrance toEntity(EntranceDTO dto);

    EntranceShortDTO toDto(Entrance entrance);
}