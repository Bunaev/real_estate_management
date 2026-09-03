package com.search_service.model.entityes.dto.in;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetroDistanceDTO {

    @NotNull(message = "ID станции метро обязателен")
    @Positive(message = "ID станции метро должен быть положительным числом")
    private Long metroStationId;

    @NotNull(message = "Расстояние до метро обязательно")
    @Positive(message = "Расстояние должно быть положительным числом")
    private Integer distance;
}
