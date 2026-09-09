package com.search_service.dto.in;

import com.search_service.entity.ApartmentType;
import com.search_service.entity.BathroomType;
import com.search_service.entity.Status;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentInDTO {

    @NotNull(message = "ID квартиры обязателен")
    @Positive(message = "ID должен быть положительным числом")
    private Long id;

    @NotNull(message = "Номер квартиры обязателен")
    @Min(value = 1, message = "Номер должен быть больше 0")
    @Max(value = 9999, message = "Номер не должен превышать 9999")
    private Integer number;

    @NotNull(message = "Этаж обязателен")
    @Min(value = 1, message = "Этаж должен быть больше 0")
    @Max(value = 200, message = "Этаж не должен превышать 200")
    private Integer floor;

    @NotNull(message = "Площадь обязательна")
    @DecimalMin(value = "1.0", message = "Площадь должна быть не менее 1 м²")
    @DecimalMax(value = "10000.0", message = "Площадь не должна превышать 10000 м²")
    private Double area;

    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", message = "Цена не может быть отрицательной")
    @DecimalMax(value = "9999999999.0", message = "Цена не должна превышать 10 миллиардов")
    private Double price;

    @DecimalMin(value = "0.0", message = "Цена за м² не может быть отрицательной")
    @DecimalMax(value = "9999999999.0", message = "Цена за м² не должна превышать 10 миллиардов")
    private Double pricePerSquareMeter;

    @NotNull(message = "Тип квартиры обязателен")
    private ApartmentType type;

    @NotNull(message = "Тип санузла обязателен")
    private BathroomType bathroomType;

    @NotNull(message = "Наличие балкона обязательно")
    private Boolean hasBalcony;

    @NotNull(message = "Статус обязателен")
    private Status status;
}
