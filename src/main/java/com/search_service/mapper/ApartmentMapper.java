package com.search_service.mapper;

import com.search_service.dto.in.ApartmentInDTO;
import com.search_service.dto.out.ApartmentDTO;
import com.search_service.entity.Apartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApartmentMapper {

    // ===== Для вывода =====
    @Mapping(target = "type", expression = "java(apartment.getType().name())")
    @Mapping(target = "bathroomType", expression = "java(apartment.getBathroomType().name())")
    @Mapping(target = "status", expression = "java(apartment.getStatus().name())")
    @Mapping(target = "entrance", source = "entrance")
    ApartmentDTO toDto(Apartment apartment);

    List<ApartmentDTO> toDtoList(List<Apartment> apartments);

    // ===== Для создания/обновления =====
    @Mapping(target = "entrance", ignore = true)
    Apartment toEntity(ApartmentInDTO dto);
}