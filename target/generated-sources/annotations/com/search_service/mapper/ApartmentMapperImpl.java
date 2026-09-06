package com.search_service.mapper;

import com.search_service.dto.in.ApartmentInDTO;
import com.search_service.dto.out.ApartmentDTO;
import com.search_service.dto.out.BuildingInfoDTO;
import com.search_service.dto.out.EntranceInfoDTO;
import com.search_service.entity.Apartment;
import com.search_service.entity.Building;
import com.search_service.entity.Entrance;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-07T02:23:48+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class ApartmentMapperImpl implements ApartmentMapper {

    @Override
    public ApartmentDTO toDto(Apartment apartment) {
        if ( apartment == null ) {
            return null;
        }

        ApartmentDTO.ApartmentDTOBuilder apartmentDTO = ApartmentDTO.builder();

        apartmentDTO.entrance( entranceToEntranceInfoDTO( apartment.getEntrance() ) );
        apartmentDTO.id( apartment.getId() );
        apartmentDTO.number( apartment.getNumber() );
        apartmentDTO.floor( apartment.getFloor() );
        apartmentDTO.area( apartment.getArea() );
        apartmentDTO.price( apartment.getPrice() );
        apartmentDTO.hasBalcony( apartment.getHasBalcony() );

        apartmentDTO.type( apartment.getType().name() );
        apartmentDTO.bathroomType( apartment.getBathroomType().name() );
        apartmentDTO.status( apartment.getStatus().name() );

        return apartmentDTO.build();
    }

    @Override
    public List<ApartmentDTO> toDtoList(List<Apartment> apartments) {
        if ( apartments == null ) {
            return null;
        }

        List<ApartmentDTO> list = new ArrayList<ApartmentDTO>( apartments.size() );
        for ( Apartment apartment : apartments ) {
            list.add( toDto( apartment ) );
        }

        return list;
    }

    @Override
    public Apartment toEntity(ApartmentInDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Apartment.ApartmentBuilder apartment = Apartment.builder();

        apartment.id( dto.getId() );
        apartment.number( dto.getNumber() );
        apartment.type( dto.getType() );
        apartment.price( dto.getPrice() );
        apartment.floor( dto.getFloor() );
        apartment.area( dto.getArea() );
        apartment.hasBalcony( dto.getHasBalcony() );
        apartment.status( dto.getStatus() );
        apartment.bathroomType( dto.getBathroomType() );

        return apartment.build();
    }

    protected BuildingInfoDTO buildingToBuildingInfoDTO(Building building) {
        if ( building == null ) {
            return null;
        }

        BuildingInfoDTO.BuildingInfoDTOBuilder buildingInfoDTO = BuildingInfoDTO.builder();

        buildingInfoDTO.id( building.getId() );
        buildingInfoDTO.name( building.getName() );

        return buildingInfoDTO.build();
    }

    protected EntranceInfoDTO entranceToEntranceInfoDTO(Entrance entrance) {
        if ( entrance == null ) {
            return null;
        }

        EntranceInfoDTO.EntranceInfoDTOBuilder entranceInfoDTO = EntranceInfoDTO.builder();

        entranceInfoDTO.id( entrance.getId() );
        entranceInfoDTO.name( entrance.getName() );
        entranceInfoDTO.building( buildingToBuildingInfoDTO( entrance.getBuilding() ) );

        return entranceInfoDTO.build();
    }
}
