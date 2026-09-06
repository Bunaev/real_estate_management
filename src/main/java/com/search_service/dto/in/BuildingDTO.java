package com.search_service.dto.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuildingDTO {

    private Long id;

    @NotBlank(message = "Название корпуса обязательно")
    @Size(max = 255, message = "Название корпуса не должно превышать 255 символов")
    private String name;

    private LocalDate completionDate;

    private LocalDate keyHandoverDate;

    @Valid
    private List<EntranceDTO> entrances;
}