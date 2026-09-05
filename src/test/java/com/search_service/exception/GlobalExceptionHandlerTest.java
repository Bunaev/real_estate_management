package com.search_service.exception;

import com.search_service.controller.ApartmentController;
import com.search_service.service.ApartmentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApartmentService apartmentService;

    @Test
    void entityNotFoundException_shouldReturn404() throws Exception {
        doThrow(new EntityNotFoundException("Квартира", 999L))
                .when(apartmentService).delete(999L);

        mockMvc.perform(delete("/api/apartments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Entity Not Found"))
                .andExpect(jsonPath("$.message").value("Квартира с ID 999 не найден(а)"));
    }

    @Test
    void generalFormatException_shouldReturn400() throws Exception {
        doThrow(new GeneralFormatException(TypeError.FILE_IS_EMPTY, "test"))
                .when(apartmentService).delete(any());

        mockMvc.perform(delete("/api/apartments/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void genericException_shouldReturn500() throws Exception {
        doThrow(new RuntimeException("Неизвестная ошибка"))
                .when(apartmentService).delete(1L);

        mockMvc.perform(delete("/api/apartments/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}