package com.search_service.model.entityes.dto.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
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

    @NotBlank(message = "Название корпуса обязательно")
    @Size(max = 255, message = "Название корпуса не должно превышать 255 символов")
    private String name;

    @Future(message = "Дата сдачи должна быть в будущем")
    private LocalDate completionDate;

    @Future(message = "Дата выдачи ключей должна быть в будущем")
    private LocalDate keyHandoverDate;

    @Valid
    private List<EntranceDTO> entrances;
}
