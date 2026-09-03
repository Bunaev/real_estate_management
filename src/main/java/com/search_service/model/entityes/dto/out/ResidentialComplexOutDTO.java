package com.search_service.model.entityes.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentialComplexOutDTO {
    private Long id;
    private String name;
    private String developer;
    private String address;
    private String location;
    private String district;
    private List<MetroDistanceOutDTO> metroDistances;
    private Integer countBuildings;
    private Integer countEntrance;
    private Integer countApartment;
}
