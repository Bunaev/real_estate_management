package com.search_service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NonPlanKeyDTO {
    private Long complexId;
    private String complexName;
    private Long buildingId;
    private String buildingName;
    private Long entranceId;
    private String entranceName;
    private Long countApartments;
}
