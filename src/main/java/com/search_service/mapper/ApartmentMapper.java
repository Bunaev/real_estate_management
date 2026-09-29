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
    @Mapping(target = "entrance", source = "entrance")
    @Mapping(target = "completionDate", source = "entrance.building.completionDate")
    @Mapping(target = "keyHandoverDate", source = "entrance.building.keyHandoverDate")
    ApartmentDTO toDto(Apartment apartment);

    List<ApartmentDTO> toDtoList(List<Apartment> apartments);

    @Mapping(target = "entrance", ignore = true)
    @Mapping(target = "price", expression = "java(dto.getPrice() == null ? null : dto.getPrice().setScale(2, java.math.BigDecimal.ROUND_HALF_UP))")
    @Mapping(target = "pricePerSquareMeter", expression = "java(dto.getPricePerSquareMeter() == null ? null : dto.getPricePerSquareMeter().setScale(2, java.math.BigDecimal.ROUND_HALF_UP))")
    Apartment toEntity(ApartmentInDTO dto);
}