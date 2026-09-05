package com.search_service.mapper;

import com.search_service.dto.in.BuildingDTO;
import com.search_service.dto.out.BuildingShortDTO;
import com.search_service.dto.out.EntranceShortDTO;
import com.search_service.entity.Building;
import com.search_service.entity.Entrance;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-05T20:06:20+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class BuildingMapperImpl implements BuildingMapper {

    @Autowired
    private EntranceMapper entranceMapper;

    @Override
    public Building toEntity(BuildingDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Building.BuildingBuilder building = Building.builder();

        building.name( dto.getName() );
        building.completionDate( dto.getCompletionDate() );
        building.keyHandoverDate( dto.getKeyHandoverDate() );

        return building.build();
    }

    @Override
    public List<BuildingShortDTO> toListDTO(List<Building> buildings) {
        if ( buildings == null ) {
            return null;
        }

        List<BuildingShortDTO> list = new ArrayList<BuildingShortDTO>( buildings.size() );
        for ( Building building : buildings ) {
            list.add( buildingToBuildingShortDTO( building ) );
        }

        return list;
    }

    protected List<EntranceShortDTO> entranceListToEntranceShortDTOList(List<Entrance> list) {
        if ( list == null ) {
            return null;
        }

        List<EntranceShortDTO> list1 = new ArrayList<EntranceShortDTO>( list.size() );
        for ( Entrance entrance : list ) {
            list1.add( entranceMapper.toDto( entrance ) );
        }

        return list1;
    }

    protected BuildingShortDTO buildingToBuildingShortDTO(Building building) {
        if ( building == null ) {
            return null;
        }

        BuildingShortDTO.BuildingShortDTOBuilder buildingShortDTO = BuildingShortDTO.builder();

        buildingShortDTO.id( building.getId() );
        buildingShortDTO.name( building.getName() );
        buildingShortDTO.entrances( entranceListToEntranceShortDTOList( building.getEntrances() ) );

        return buildingShortDTO.build();
    }
}
