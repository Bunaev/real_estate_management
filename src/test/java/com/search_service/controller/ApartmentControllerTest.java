package com.search_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.in.ApartmentInDTO;
import com.search_service.dto.out.ApartmentDTO;
import com.search_service.dto.out.EntranceInfoDTO;
import com.search_service.dto.out.BuildingInfoDTO;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.service.ApartmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ApartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ApartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ApartmentService apartmentService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void deleteApartment_shouldReturn204() throws Exception {
        doNothing().when(apartmentService).delete(1L);

        mockMvc.perform(delete("/api/apartments/1"))
                .andExpect(status().isNoContent());

        verify(apartmentService).delete(1L);
    }

    @Test
    void deleteApartment_shouldReturn404_whenNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Квартира", 999L))
                .when(apartmentService).delete(999L);

        mockMvc.perform(delete("/api/apartments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Entity Not Found"));
    }

    @Test
    void getFilteredApartments_shouldReturnPage() throws Exception {
        ApartmentDTO dto = ApartmentDTO.builder()
                .id(1L).number(10).floor(3).area(45.0).price(5_000_000.0)
                .type("ONE_ROOM").bathroomType("COMBINED").hasBalcony(true).status("AVAILABLE")
                .entrance(EntranceInfoDTO.builder()
                        .id(1L).name("Секция 1")
                        .building(BuildingInfoDTO.builder().id(1L).name("Корпус 1").build())
                        .build())
                .build();
        Page<ApartmentDTO> page = new PageImpl<>(List.of(dto));

        when(apartmentService.getFilteredApartment(any(FilterDTO.class), any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/apartments/filter")
                        .param("residentialComplexId", "1")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].number").value(10));
    }

    @Test
    void updateApartment_shouldReturnUpdatedDto() throws Exception {
        ApartmentInDTO input = ApartmentInDTO.builder()
                .id(1L).number(20).floor(5).area(60.0).price(7_000_000.0)
                .type(com.search_service.entity.ApartmentType.ONE_ROOM)
                .bathroomType(com.search_service.entity.BathroomType.COMBINED)
                .hasBalcony(false)
                .status(com.search_service.entity.Status.AVAILABLE)
                .build();

        ApartmentDTO output = ApartmentDTO.builder()
                .id(1L).number(20).floor(5).area(60.0).price(7_000_000.0)
                .type("ONE_ROOM").bathroomType("COMBINED").hasBalcony(false).status("AVAILABLE")
                .build();

        when(apartmentService.update(any(ApartmentInDTO.class))).thenReturn(output);

        mockMvc.perform(put("/api/apartments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(20))
                .andExpect(jsonPath("$.floor").value(5));
    }
}