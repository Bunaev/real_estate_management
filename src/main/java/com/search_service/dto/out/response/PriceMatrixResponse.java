package com.search_service.dto.out.response;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class PriceMatrixResponse {
    private Integer numberApartment;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private BigDecimal diff;

    @Builder
    public PriceMatrixResponse(Integer numberApartment, BigDecimal oldPrice, BigDecimal newPrice) {
        this.numberApartment = numberApartment;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.diff = newPrice.subtract(oldPrice);
    }

}
