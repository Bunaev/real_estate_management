package com.search_service.dto.in;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentialComplexDTO {

    private Long id;

    @NotBlank(message = "Название ЖК обязательно")
    @Size(max = 255, message = "Название не должно превышать 255 символов")
    private String name;

    @NotBlank(message = "Адрес обязателен")
    @Size(max = 500, message = "Адрес не должен превышать 500 символов")
    private String address;

    @NotNull(message = "ID района обязателен")
    @Positive(message = "ID района должен быть положительным числом")
    private Long districtId;

    @NotNull(message = "ID застройщика обязателен")
    @Positive(message = "ID застройщика должен быть положительным числом")
    private Long developerId;

    @Valid
    private List<MetroDistanceDTO> metroStations;

    @Valid
    private List<BuildingDTO> buildings;
    @NotNull(message = "Без этого параметра ЖК не будет отображаться на карте")
    private Double latitude;
    @NotNull(message = "Без этого параметра ЖК не будет отображаться на карте")
    private Double longitude;
}