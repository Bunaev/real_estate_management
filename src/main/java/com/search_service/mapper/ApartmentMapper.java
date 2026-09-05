package com.search_service.mapper;

import com.search_service.dto.in.ApartmentInDTO;
import com.search_service.dto.out.ApartmentDTO;
import com.search_service.entity.Apartment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApartmentMapper {
    @Mapping(target = "type", expression = "java(apartment.getType().name())")
    @Mapping(target = "bathroomType", expression = "java(apartment.getBathroomType().name())")
    @Mapping(target = "status", expression = "java(apartment.getStatus().name())")
    ApartmentDTO toDto(Apartment apartment);

    List<ApartmentDTO> toListDtoList(List<Apartment> apartments);

    Apartment toEntity(ApartmentInDTO apartment);
}