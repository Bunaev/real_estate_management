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
public class ResidentialComplexDetailDTO {
    private Long id;
    private String name;
    private String address;
    private String developer;
    private String district;
    private String location;
    private List<BuildingShortDTO> buildings;
    private Double latitude;
    private Double longitude;
}