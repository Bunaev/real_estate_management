package com.search_service.mapper;

import com.search_service.dto.in.EntranceDTO;
import com.search_service.dto.out.BuildingInfoDTO;
import com.search_service.dto.out.EntranceInfoDTO;
import com.search_service.dto.out.EntranceShortDTO;
import com.search_service.dto.out.ResidentialComplexEditDTO;
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
public class EntranceMapperImpl implements EntranceMapper {

    @Override
    public Entrance toEntity(EntranceDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Entrance.EntranceBuilder entrance = Entrance.builder();

        entrance.id( dto.getId() );
        entrance.name( dto.getName() );

        return entrance.build();
    }

    @Override
    public List<Entrance> toEntityList(List<EntranceDTO> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<Entrance> list = new ArrayList<Entrance>( dtos.size() );
        for ( EntranceDTO entranceDTO : dtos ) {
            list.add( toEntity( entranceDTO ) );
        }

        return list;
    }

    @Override
    public EntranceShortDTO toShortDto(Entrance entrance) {
        if ( entrance == null ) {
            return null;
        }

        EntranceShortDTO.EntranceShortDTOBuilder entranceShortDTO = EntranceShortDTO.builder();

        entranceShortDTO.id( entrance.getId() );
        entranceShortDTO.name( entrance.getName() );

        return entranceShortDTO.build();
    }

    @Override
    public List<EntranceShortDTO> toShortDtoList(List<Entrance> entrances) {
        if ( entrances == null ) {
            return null;
        }

        List<EntranceShortDTO> list = new ArrayList<EntranceShortDTO>( entrances.size() );
        for ( Entrance entrance : entrances ) {
            list.add( toShortDto( entrance ) );
        }

        return list;
    }

    @Override
    public ResidentialComplexEditDTO.EntranceEditDTO toEditDto(Entrance entrance) {
        if ( entrance == null ) {
            return null;
        }

        ResidentialComplexEditDTO.EntranceEditDTO.EntranceEditDTOBuilder entranceEditDTO = ResidentialComplexEditDTO.EntranceEditDTO.builder();

        entranceEditDTO.maxFloor( entrance.getMaxFloors() );
        entranceEditDTO.id( entrance.getId() );
        entranceEditDTO.name( entrance.getName() );

        return entranceEditDTO.build();
    }

    @Override
    public List<ResidentialComplexEditDTO.EntranceEditDTO> toEditDtoList(List<Entrance> entrances) {
        if ( entrances == null ) {
            return null;
        }

        List<ResidentialComplexEditDTO.EntranceEditDTO> list = new ArrayList<ResidentialComplexEditDTO.EntranceEditDTO>( entrances.size() );
        for ( Entrance entrance : entrances ) {
            list.add( toEditDto( entrance ) );
        }

        return list;
    }

    @Override
    public EntranceInfoDTO toInfoDto(Entrance entrance) {
        if ( entrance == null ) {
            return null;
        }

        EntranceInfoDTO.EntranceInfoDTOBuilder entranceInfoDTO = EntranceInfoDTO.builder();

        entranceInfoDTO.building( buildingToBuildingInfoDTO( entrance.getBuilding() ) );
        entranceInfoDTO.id( entrance.getId() );
        entranceInfoDTO.name( entrance.getName() );

        return entranceInfoDTO.build();
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
}
