package com.search_service.mapper;

import com.search_service.dto.in.BuildingDTO;
import com.search_service.dto.out.BuildingInfoDTO;
import com.search_service.dto.out.BuildingShortDTO;
import com.search_service.dto.out.ResidentialComplexEditDTO;
import com.search_service.entity.Building;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-07T02:23:48+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
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

        building.id( dto.getId() );
        building.name( dto.getName() );
        building.entrances( entranceMapper.toEntityList( dto.getEntrances() ) );
        building.completionDate( dto.getCompletionDate() );
        building.keyHandoverDate( dto.getKeyHandoverDate() );

        return building.build();
    }

    @Override
    public List<Building> toEntityList(List<BuildingDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Building> list = new ArrayList<Building>( dtos.size() );
        for ( BuildingDTO buildingDTO : dtos ) {
            list.add( toEntity( buildingDTO ) );
        }

        return list;
    }

    @Override
    public BuildingShortDTO toShortDto(Building building) {
        if ( building == null ) {
            return null;
        }

        BuildingShortDTO.BuildingShortDTOBuilder buildingShortDTO = BuildingShortDTO.builder();

        buildingShortDTO.entrances( entranceMapper.toShortDtoList( building.getEntrances() ) );
        buildingShortDTO.id( building.getId() );
        buildingShortDTO.name( building.getName() );

        return buildingShortDTO.build();
    }

    @Override
    public List<BuildingShortDTO> toShortDtoList(List<Building> buildings) {
        if ( buildings == null ) {
            return null;
        }

        List<BuildingShortDTO> list = new ArrayList<BuildingShortDTO>( buildings.size() );
        for ( Building building : buildings ) {
            list.add( toShortDto( building ) );
        }

        return list;
    }

    @Override
    public ResidentialComplexEditDTO.BuildingEditDTO toEditDto(Building building) {
        if ( building == null ) {
            return null;
        }

        ResidentialComplexEditDTO.BuildingEditDTO.BuildingEditDTOBuilder buildingEditDTO = ResidentialComplexEditDTO.BuildingEditDTO.builder();

        buildingEditDTO.entrances( entranceMapper.toEditDtoList( building.getEntrances() ) );
        buildingEditDTO.id( building.getId() );
        buildingEditDTO.name( building.getName() );
        buildingEditDTO.completionDate( building.getCompletionDate() );
        buildingEditDTO.keyHandoverDate( building.getKeyHandoverDate() );

        return buildingEditDTO.build();
    }

    @Override
    public List<ResidentialComplexEditDTO.BuildingEditDTO> toEditDtoList(List<Building> buildings) {
        if ( buildings == null ) {
            return null;
        }

        List<ResidentialComplexEditDTO.BuildingEditDTO> list = new ArrayList<ResidentialComplexEditDTO.BuildingEditDTO>( buildings.size() );
        for ( Building building : buildings ) {
            list.add( toEditDto( building ) );
        }

        return list;
    }

    @Override
    public BuildingInfoDTO toInfoDto(Building building) {
        if ( building == null ) {
            return null;
        }

        BuildingInfoDTO.BuildingInfoDTOBuilder buildingInfoDTO = BuildingInfoDTO.builder();

        buildingInfoDTO.id( building.getId() );
        buildingInfoDTO.name( building.getName() );

        return buildingInfoDTO.build();
    }
}
