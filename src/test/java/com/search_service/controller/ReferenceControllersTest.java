package com.search_service.controller;

import com.search_service.entity.Developer;
import com.search_service.entity.Location;
import com.search_service.entity.MetroStation;
import com.search_service.mapper.ReferenceMapperImpl;
import com.search_service.service.DeveloperService;
import com.search_service.service.LocationService;
import com.search_service.service.MetroService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LocationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ReferenceMapperImpl.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Test
    void getLocations_shouldReturnList() throws Exception {
        when(locationService.findAll()).thenReturn(List.of(
                Location.builder().id(1L).name("Санкт-Петербург").build()
        ));

        mockMvc.perform(get("/api/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Санкт-Петербург"));
    }
}

@WebMvcTest(controllers = DevelopersController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ReferenceMapperImpl.class)
class DevelopersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeveloperService developerService;

    @Test
    void getDevelopers_shouldReturnList() throws Exception {
        when(developerService.findAll()).thenReturn(List.of(
                Developer.builder().id(1L).name("Главстрой").build()
        ));

        mockMvc.perform(get("/api/developers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Главстрой"));
    }
}

@WebMvcTest(controllers = MetroStationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(ReferenceMapperImpl.class)
class MetroStationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MetroService metroService;

    @Test
    void getMetroStations_shouldReturnList() throws Exception {
        when(metroService.findAll()).thenReturn(List.of(
                MetroStation.builder().id(1L).name("Невский проспект").build()
        ));

        mockMvc.perform(get("/api/metro-stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Невский проспект"));
    }
}