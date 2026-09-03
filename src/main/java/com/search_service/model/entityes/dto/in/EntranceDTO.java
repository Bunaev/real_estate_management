package com.search_service.model.entityes.dto.in;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntranceDTO {

    @NotBlank(message = "Название секции обязательно")
    @Size(max = 255, message = "Название секции не должно превышать 255 символов")
    private String name;

    @NotNull(message = "Максимальное количество этажей обязательно")
    @Positive(message = "Количество этажей должно быть положительным числом")
    private Integer maxFloor;
}
