package com.search_service.model.entityes.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

    public ResidentialComplexOutDTO(Long id, String name, String developer,
                                    String address, String location, String district,
                                    int countBuildings, int countEntrance, int countApartment) {
        this.id = id;
        this.name = name;
        this.developer = developer;
        this.address = address;
        this.location = location;
        this.district = district;
        this.countBuildings = countBuildings;
        this.countEntrance = countEntrance;
        this.countApartment = countApartment;
        this.metroDistances = new ArrayList<>(); // или null, если не нужны
    }
}
