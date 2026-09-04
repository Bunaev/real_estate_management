package com.search_service.model.entityes.dto;

import com.search_service.model.entityes.Entrance;
import com.search_service.model.entityes.dto.in.EntranceDTO;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-03T21:48:28+0300",
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

        entrance.name( dto.getName() );

        return entrance.build();
    }
}
