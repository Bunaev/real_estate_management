package com.search_service.dto.in;

import com.search_service.entity.TypePriceChange;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchedulePriceChangeRequestDTO {

    @NotNull(message = "Дата выполнения обязательна")
    @Future(message = "Дата выполнения должна быть в будущем")
    private LocalDateTime deadLine;

    private boolean publishNews;

    @NotNull(message = "Фильтр обязателен")
    private FilterDTO filter;

    @NotNull(message = "Тип изменения цены обязателен")
    private TypePriceChange typeChange;

    @NotNull(message = "Значение обязательно")
    private BigDecimal value;

    @NotNull(message = "Направление операции обязательно")
    private Boolean arithmeticalOperation;
}
