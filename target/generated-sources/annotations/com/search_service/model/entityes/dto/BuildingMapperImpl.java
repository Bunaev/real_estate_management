package com.search_service.model.entityes.dto;

import com.search_service.model.entityes.Building;
import com.search_service.model.entityes.dto.in.BuildingDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-03T21:48:28+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class BuildingMapperImpl implements BuildingMapper {

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
}
