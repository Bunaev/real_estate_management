package com.search_service.model.entityes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class MetroDistanceDto {
    private Long metroStationId;
    private Integer distance;

}

