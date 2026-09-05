package com.search_service.mapper;

import com.search_service.dto.out.DeveloperDTO;
import com.search_service.dto.out.DistrictDTO;
import com.search_service.dto.out.LocationDTO;
import com.search_service.dto.out.MetroStationDTO;
import com.search_service.entity.Developer;
import com.search_service.entity.District;
import com.search_service.entity.Location;
import com.search_service.entity.MetroStation;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-05T16:57:06+0300",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.12 (Oracle Corporation)"
)
@Component
public class ReferenceMapperImpl implements ReferenceMapper {

    @Override
    public DeveloperDTO toDeveloperDto(Developer developer) {
        if ( developer == null ) {
            return null;
        }

        DeveloperDTO.DeveloperDTOBuilder developerDTO = DeveloperDTO.builder();

        developerDTO.id( developer.getId() );
        developerDTO.name( developer.getName() );

        return developerDTO.build();
    }

    @Override
    public List<DeveloperDTO> toDeveloperDtoList(List<Developer> developers) {
        if ( developers == null ) {
            return null;
        }

        List<DeveloperDTO> list = new ArrayList<DeveloperDTO>( developers.size() );
        for ( Developer developer : developers ) {
            list.add( toDeveloperDto( developer ) );
        }

        return list;
    }

    @Override
    public LocationDTO toLocationDto(Location location) {
        if ( location == null ) {
            return null;
        }

        LocationDTO.LocationDTOBuilder locationDTO = LocationDTO.builder();

        locationDTO.id( location.getId() );
        locationDTO.name( location.getName() );

        return locationDTO.build();
    }

    @Override
    public List<LocationDTO> toLocationDtoList(List<Location> locations) {
        if ( locations == null ) {
            return null;
        }

        List<LocationDTO> list = new ArrayList<LocationDTO>( locations.size() );
        for ( Location location : locations ) {
            list.add( toLocationDto( location ) );
        }

        return list;
    }

    @Override
    public DistrictDTO toDistrictDto(District district) {
        if ( district == null ) {
            return null;
        }

        DistrictDTO.DistrictDTOBuilder districtDTO = DistrictDTO.builder();

        districtDTO.id( district.getId() );
        districtDTO.name( district.getName() );
        districtDTO.location( toLocationDto( district.getLocation() ) );

        return districtDTO.build();
    }

    @Override
    public List<DistrictDTO> toDistrictDtoList(List<District> districts) {
        if ( districts == null ) {
            return null;
        }

        List<DistrictDTO> list = new ArrayList<DistrictDTO>( districts.size() );
        for ( District district : districts ) {
            list.add( toDistrictDto( district ) );
        }

        return list;
    }

    @Override
    public MetroStationDTO toMetroStationDto(MetroStation metroStation) {
        if ( metroStation == null ) {
            return null;
        }

        MetroStationDTO.MetroStationDTOBuilder metroStationDTO = MetroStationDTO.builder();

        metroStationDTO.id( metroStation.getId() );
        metroStationDTO.name( metroStation.getName() );

        return metroStationDTO.build();
    }

    @Override
    public List<MetroStationDTO> toMetroStationDtoList(List<MetroStation> metroStations) {
        if ( metroStations == null ) {
            return null;
        }

        List<MetroStationDTO> list = new ArrayList<MetroStationDTO>( metroStations.size() );
        for ( MetroStation metroStation : metroStations ) {
            list.add( toMetroStationDto( metroStation ) );
        }

        return list;
    }
}
