package com.search_service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplexCardOutDTO {
    private Long id;
    private String name;
    private String address;
    private String locationName;
    private String districtName;
    private String developerName;
    private Double latitude;
    private Double longitude;
    private String keyRenderPath;
    private Integer countBuildings;
    private Integer countEntrance;
    private Integer countApartment;
    private List<MetroDistanceOutDTO> metroDistances;
}
