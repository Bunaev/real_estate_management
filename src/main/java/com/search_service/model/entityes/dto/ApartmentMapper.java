package com.search_service.model.entityes.dto;

import com.search_service.model.entityes.Apartment;
import com.search_service.model.entityes.dto.in.ApartmentInDTO;
import com.search_service.model.entityes.dto.out.ApartmentDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApartmentMapper {
    ApartmentDTO toDto(Apartment apartment);
    List<ApartmentDTO> toListDtoList(List<Apartment> apartments);

    Apartment toEntity(ApartmentInDTO apartment);
}
