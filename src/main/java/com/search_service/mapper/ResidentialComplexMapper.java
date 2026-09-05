package com.search_service.mapper;

import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.entity.ResidentialComplex;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResidentialComplexMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "district", ignore = true)
    @Mapping(target = "developer", ignore = true)
    @Mapping(target = "metroDistances", ignore = true)
    @Mapping(target = "buildings", ignore = true)
    ResidentialComplex toEntity(ResidentialComplexDTO dto);

    ResidentialComplexDTO toDTO(ResidentialComplex entity);
}