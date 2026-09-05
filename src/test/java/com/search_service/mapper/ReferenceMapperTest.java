package com.search_service.mapper;

import com.search_service.dto.out.DeveloperDTO;
import com.search_service.dto.out.LocationDTO;
import com.search_service.dto.out.MetroStationDTO;
import com.search_service.entity.Developer;
import com.search_service.entity.Location;
import com.search_service.entity.MetroStation;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReferenceMapperTest {

    private final ReferenceMapper mapper = Mappers.getMapper(ReferenceMapper.class);

    @Test
    void toDeveloperDto_shouldMapCorrectly() {
        Developer developer = Developer.builder().id(1L).name("Главстрой").build();
        DeveloperDTO dto = mapper.toDeveloperDto(developer);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Главстрой");
    }

    @Test
    void toDeveloperDtoList_shouldMapList() {
        List<Developer> developers = List.of(
                Developer.builder().id(1L).name("Dev1").build(),
                Developer.builder().id(2L).name("Dev2").build()
        );
        List<DeveloperDTO> dtos = mapper.toDeveloperDtoList(developers);
        assertThat(dtos).hasSize(2);
    }

    @Test
    void toLocationDto_shouldMapCorrectly() {
        Location location = Location.builder().id(1L).name("Санкт-Петербург").build();
        LocationDTO dto = mapper.toLocationDto(location);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Санкт-Петербург");
    }

    @Test
    void toMetroStationDto_shouldMapCorrectly() {
        MetroStation station = MetroStation.builder().id(1L).name("Невский проспект").build();
        MetroStationDTO dto = mapper.toMetroStationDto(station);
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getName()).isEqualTo("Невский проспект");
    }
}