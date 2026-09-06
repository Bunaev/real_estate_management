package com.search_service.mapper;

import com.search_service.dto.in.EntranceDTO;
import com.search_service.dto.out.EntranceInfoDTO;
import com.search_service.dto.out.EntranceShortDTO;
import com.search_service.dto.out.ResidentialComplexEditDTO;
import com.search_service.entity.Entrance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EntranceMapper {

    // === ДЛЯ СОЗДАНИЯ/ОБНОВЛЕНИЯ ===
    @Mapping(target = "building", ignore = true)
    @Mapping(target = "apartments", ignore = true)
    Entrance toEntity(EntranceDTO dto);

    List<Entrance> toEntityList(List<EntranceDTO> dtos);

    // === ДЛЯ ВЫВОДА В СПИСОК ===
    EntranceShortDTO toShortDto(Entrance entrance);

    List<EntranceShortDTO> toShortDtoList(List<Entrance> entrances);

    // === ДЛЯ РЕДАКТИРОВАНИЯ ===
    @Mapping(target = "maxFloor", source = "maxFloors")
    ResidentialComplexEditDTO.EntranceEditDTO toEditDto(Entrance entrance);

    List<ResidentialComplexEditDTO.EntranceEditDTO> toEditDtoList(List<Entrance> entrances);

    // === ДЛЯ ИНФОРМАЦИИ О СЕКЦИИ В КВАРТИРЕ ===
    @Mapping(target = "building", source = "building")
    EntranceInfoDTO toInfoDto(Entrance entrance);
}