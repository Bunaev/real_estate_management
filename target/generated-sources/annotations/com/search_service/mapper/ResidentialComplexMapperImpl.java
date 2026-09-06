package com.search_service.mapper;

import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.ResidentialComplexDetailDTO;
import com.search_service.dto.out.ResidentialComplexEditDTO;
import com.search_service.dto.out.ResidentialComplexOutDTO;
import com.search_service.dto.out.ResidentialComplexShortDTO;
import com.search_service.entity.Developer;
import com.search_service.entity.District;
import com.search_service.entity.Location;
import com.search_service.entity.ResidentialComplex;
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
public class ResidentialComplexMapperImpl implements ResidentialComplexMapper {

    @Autowired
    private BuildingMapper buildingMapper;
    @Autowired
    private MetroDistanceMapper metroDistanceMapper;

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
    public ResidentialComplexOutDTO toOutDto(ResidentialComplex complex) {
        if ( complex == null ) {
            return null;
        }

        ResidentialComplexOutDTO.ResidentialComplexOutDTOBuilder residentialComplexOutDTO = ResidentialComplexOutDTO.builder();

        residentialComplexOutDTO.developer( complexDeveloperName( complex ) );
        residentialComplexOutDTO.location( complexDistrictLocationName( complex ) );
        residentialComplexOutDTO.district( complexDistrictName( complex ) );
        residentialComplexOutDTO.metroDistances( metroDistanceMapper.toOutDtoList( complex.getMetroDistances() ) );
        residentialComplexOutDTO.id( complex.getId() );
        residentialComplexOutDTO.name( complex.getName() );
        residentialComplexOutDTO.address( complex.getAddress() );

        residentialComplexOutDTO.countBuildings( complex.getBuildings() != null ? complex.getBuildings().size() : 0 );
        residentialComplexOutDTO.countEntrance( calculateEntranceCount(complex) );
        residentialComplexOutDTO.countApartment( calculateApartmentCount(complex) );

        return residentialComplexOutDTO.build();
    }

    @Override
    public List<ResidentialComplexOutDTO> toOutDtoList(List<ResidentialComplex> complexes) {
        if ( complexes == null ) {
            return null;
        }

        List<ResidentialComplexOutDTO> list = new ArrayList<ResidentialComplexOutDTO>( complexes.size() );
        for ( ResidentialComplex residentialComplex : complexes ) {
            list.add( toOutDto( residentialComplex ) );
        }

        return list;
    }

    @Override
    public ResidentialComplexShortDTO toShortDto(ResidentialComplex complex) {
        if ( complex == null ) {
            return null;
        }

        ResidentialComplexShortDTO.ResidentialComplexShortDTOBuilder residentialComplexShortDTO = ResidentialComplexShortDTO.builder();

        residentialComplexShortDTO.developer( complexDeveloperName( complex ) );
        residentialComplexShortDTO.id( complex.getId() );
        residentialComplexShortDTO.name( complex.getName() );

        residentialComplexShortDTO.fullAddress( buildFullAddress(complex) );

        return residentialComplexShortDTO.build();
    }

    @Override
    public ResidentialComplexDetailDTO toDetailDto(ResidentialComplex complex) {
        if ( complex == null ) {
            return null;
        }

        ResidentialComplexDetailDTO.ResidentialComplexDetailDTOBuilder residentialComplexDetailDTO = ResidentialComplexDetailDTO.builder();

        residentialComplexDetailDTO.developer( complexDeveloperName( complex ) );
        residentialComplexDetailDTO.district( complexDistrictName( complex ) );
        residentialComplexDetailDTO.location( complexDistrictLocationName( complex ) );
        residentialComplexDetailDTO.buildings( buildingMapper.toShortDtoList( complex.getBuildings() ) );
        residentialComplexDetailDTO.id( complex.getId() );
        residentialComplexDetailDTO.name( complex.getName() );
        residentialComplexDetailDTO.address( complex.getAddress() );

        return residentialComplexDetailDTO.build();
    }

    @Override
    public ResidentialComplexEditDTO toEditDto(ResidentialComplex complex) {
        if ( complex == null ) {
            return null;
        }

        ResidentialComplexEditDTO.ResidentialComplexEditDTOBuilder residentialComplexEditDTO = ResidentialComplexEditDTO.builder();

        residentialComplexEditDTO.locationId( complexDistrictLocationId( complex ) );
        residentialComplexEditDTO.districtId( complexDistrictId( complex ) );
        residentialComplexEditDTO.developerId( complexDeveloperId( complex ) );
        residentialComplexEditDTO.locationName( complexDistrictLocationName( complex ) );
        residentialComplexEditDTO.districtName( complexDistrictName( complex ) );
        residentialComplexEditDTO.developerName( complexDeveloperName( complex ) );
        residentialComplexEditDTO.metroDistances( metroDistanceMapper.toEditDtoList( complex.getMetroDistances() ) );
        residentialComplexEditDTO.buildings( buildingMapper.toEditDtoList( complex.getBuildings() ) );
        residentialComplexEditDTO.id( complex.getId() );
        residentialComplexEditDTO.name( complex.getName() );
        residentialComplexEditDTO.address( complex.getAddress() );

        return residentialComplexEditDTO.build();
    }

    private String complexDeveloperName(ResidentialComplex residentialComplex) {
        if ( residentialComplex == null ) {
            return null;
        }
        Developer developer = residentialComplex.getDeveloper();
        if ( developer == null ) {
            return null;
        }
        String name = developer.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String complexDistrictLocationName(ResidentialComplex residentialComplex) {
        if ( residentialComplex == null ) {
            return null;
        }
        District district = residentialComplex.getDistrict();
        if ( district == null ) {
            return null;
        }
        Location location = district.getLocation();
        if ( location == null ) {
            return null;
        }
        String name = location.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private String complexDistrictName(ResidentialComplex residentialComplex) {
        if ( residentialComplex == null ) {
            return null;
        }
        District district = residentialComplex.getDistrict();
        if ( district == null ) {
            return null;
        }
        String name = district.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

    private Long complexDistrictLocationId(ResidentialComplex residentialComplex) {
        if ( residentialComplex == null ) {
            return null;
        }
        District district = residentialComplex.getDistrict();
        if ( district == null ) {
            return null;
        }
        Location location = district.getLocation();
        if ( location == null ) {
            return null;
        }
        Long id = location.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long complexDistrictId(ResidentialComplex residentialComplex) {
        if ( residentialComplex == null ) {
            return null;
        }
        District district = residentialComplex.getDistrict();
        if ( district == null ) {
            return null;
        }
        Long id = district.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }

    private Long complexDeveloperId(ResidentialComplex residentialComplex) {
        if ( residentialComplex == null ) {
            return null;
        }
        Developer developer = residentialComplex.getDeveloper();
        if ( developer == null ) {
            return null;
        }
        Long id = developer.getId();
        if ( id == null ) {
            return null;
        }
        return id;
    }
}
