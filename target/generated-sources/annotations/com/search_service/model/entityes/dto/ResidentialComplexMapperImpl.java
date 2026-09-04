package com.search_service.model.entityes.dto;

import com.search_service.model.entityes.Building;
import com.search_service.model.entityes.Entrance;
import com.search_service.model.entityes.ResidentialComplex;
import com.search_service.model.entityes.dto.in.BuildingDTO;
import com.search_service.model.entityes.dto.in.EntranceDTO;
import com.search_service.model.entityes.dto.in.ResidentialComplexDTO;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-04T17:57:56+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class ResidentialComplexMapperImpl implements ResidentialComplexMapper {

    @Override
    public ResidentialComplex toEntity(ResidentialComplexDTO dto) {
        if ( dto == null ) {
            return null;
        }

        ResidentialComplex.ResidentialComplexBuilder residentialComplex = ResidentialComplex.builder();

        residentialComplex.name( dto.getName() );
        residentialComplex.address( dto.getAddress() );

        return residentialComplex.build();
    }

    @Override
    public ResidentialComplexDTO toDTO(ResidentialComplex entity) {
        if ( entity == null ) {
            return null;
        }

        ResidentialComplexDTO.ResidentialComplexDTOBuilder residentialComplexDTO = ResidentialComplexDTO.builder();

        residentialComplexDTO.name( entity.getName() );
        residentialComplexDTO.address( entity.getAddress() );
        residentialComplexDTO.buildings( buildingListToBuildingDTOList( entity.getBuildings() ) );

        return residentialComplexDTO.build();
    }

    protected EntranceDTO entranceToEntranceDTO(Entrance entrance) {
        if ( entrance == null ) {
            return null;
        }

        EntranceDTO.EntranceDTOBuilder entranceDTO = EntranceDTO.builder();

        entranceDTO.name( entrance.getName() );

        return entranceDTO.build();
    }

    protected List<EntranceDTO> entranceListToEntranceDTOList(List<Entrance> list) {
        if ( list == null ) {
            return null;
        }

        List<EntranceDTO> list1 = new ArrayList<EntranceDTO>( list.size() );
        for ( Entrance entrance : list ) {
            list1.add( entranceToEntranceDTO( entrance ) );
        }

        return list1;
    }

    protected BuildingDTO buildingToBuildingDTO(Building building) {
        if ( building == null ) {
            return null;
        }

        BuildingDTO.BuildingDTOBuilder buildingDTO = BuildingDTO.builder();

        buildingDTO.name( building.getName() );
        buildingDTO.completionDate( building.getCompletionDate() );
        buildingDTO.keyHandoverDate( building.getKeyHandoverDate() );
        buildingDTO.entrances( entranceListToEntranceDTOList( building.getEntrances() ) );

        return buildingDTO.build();
    }

    protected List<BuildingDTO> buildingListToBuildingDTOList(List<Building> list) {
        if ( list == null ) {
            return null;
        }

        List<BuildingDTO> list1 = new ArrayList<BuildingDTO>( list.size() );
        for ( Building building : list ) {
            list1.add( buildingToBuildingDTO( building ) );
        }

        return list1;
    }
}
