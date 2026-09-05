package com.search_service.mapper;

import com.search_service.dto.in.EntranceDTO;
import com.search_service.dto.out.EntranceShortDTO;
import com.search_service.entity.Entrance;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-05T21:41:57+0300",
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

    @Override
    public EntranceShortDTO toDto(Entrance entrance) {
        if ( entrance == null ) {
            return null;
        }

        EntranceShortDTO.EntranceShortDTOBuilder entranceShortDTO = EntranceShortDTO.builder();

        entranceShortDTO.id( entrance.getId() );
        entranceShortDTO.name( entrance.getName() );

        return entranceShortDTO.build();
    }
}
