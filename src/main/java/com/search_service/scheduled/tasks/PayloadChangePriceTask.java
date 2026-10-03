package com.search_service.scheduled.tasks;

import com.search_service.dto.in.FilterDTO;
import com.search_service.entity.TypePriceChange;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PayloadChangePriceTask {
    private FilterDTO filterDTO;
    private TypePriceChange typeChange;
    private BigDecimal value;
    private Boolean arithmeticalOperation;

}
