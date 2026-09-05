package com.search_service.mapper;

import com.search_service.dto.out.DeveloperDTO;
import com.search_service.dto.out.DistrictDTO;
import com.search_service.dto.out.LocationDTO;
import com.search_service.dto.out.MetroStationDTO;
import com.search_service.entity.Developer;
import com.search_service.entity.District;
import com.search_service.entity.Location;
import com.search_service.entity.MetroStation;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReferenceMapper {
    DeveloperDTO toDeveloperDto(Developer developer);
    List<DeveloperDTO> toDeveloperDtoList(List<Developer> developers);

    LocationDTO toLocationDto(Location location);
    List<LocationDTO> toLocationDtoList(List<Location> locations);

    DistrictDTO toDistrictDto(District district);
    List<DistrictDTO> toDistrictDtoList(List<District> districts);

    MetroStationDTO toMetroStationDto(MetroStation metroStation);
    List<MetroStationDTO> toMetroStationDtoList(List<MetroStation> metroStations);
}