package com.search_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.ResidentialComplexShortDTO;
import com.search_service.dto.out.SearchSuggestionDTO;
import com.search_service.service.ResidentialComplexService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ComplexController.class)
@AutoConfigureMockMvc(addFilters = false)
class ComplexControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResidentialComplexService complexService;

    @Test
    void suggest_shouldReturnSuggestions() throws Exception {
        when(complexService.suggest(anyString(), anyInt()))
                .thenReturn(List.of(SearchSuggestionDTO.builder()
                        .entityId(1L).text("Vertical Московская").entityType("ЖК").build()));

        mockMvc.perform(get("/api/complexes/suggest").param("q", "моск").param("limit", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].text").value("Vertical Московская"));
    }

    @Test
    void createComplex_shouldReturnCreated() throws Exception {
        ResidentialComplexDTO input = ResidentialComplexDTO.builder().id(1L).name("ЖК Новый").address("ул. Новая").districtId(1L).developerId(1L).build();
        when(complexService.create(any(ResidentialComplexDTO.class)))
                .thenReturn(ResidentialComplexShortDTO.builder().id(1L).name("ЖК Новый").build());

        mockMvc.perform(post("/api/complexes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void deleteComplex_shouldReturnNoContent() throws Exception {
        doNothing().when(complexService).delete(1L);

        mockMvc.perform(delete("/api/complexes/1"))
                .andExpect(status().isNoContent());
    }

}
