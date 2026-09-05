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
    date = "2026-09-05T21:41:56+0300",
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

        apartmentDTO.id( apartment.getId() );
        apartmentDTO.number( apartment.getNumber() );
        apartmentDTO.floor( apartment.getFloor() );
        apartmentDTO.area( apartment.getArea() );
        apartmentDTO.price( apartment.getPrice() );
        apartmentDTO.hasBalcony( apartment.getHasBalcony() );
        apartmentDTO.entrance( entranceToEntranceInfoDTO( apartment.getEntrance() ) );

        apartmentDTO.type( apartment.getType().name() );
        apartmentDTO.bathroomType( apartment.getBathroomType().name() );
        apartmentDTO.status( apartment.getStatus().name() );

        return apartmentDTO.build();
    }

    @Override
    public List<ApartmentDTO> toListDtoList(List<Apartment> apartments) {
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
    public Apartment toEntity(ApartmentInDTO apartment) {
        if ( apartment == null ) {
            return null;
        }

        Apartment.ApartmentBuilder apartment1 = Apartment.builder();

        apartment1.id( apartment.getId() );
        apartment1.number( apartment.getNumber() );
        apartment1.type( apartment.getType() );
        apartment1.price( apartment.getPrice() );
        apartment1.floor( apartment.getFloor() );
        apartment1.area( apartment.getArea() );
        apartment1.hasBalcony( apartment.getHasBalcony() );
        apartment1.status( apartment.getStatus() );
        apartment1.bathroomType( apartment.getBathroomType() );

        return apartment1.build();
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
