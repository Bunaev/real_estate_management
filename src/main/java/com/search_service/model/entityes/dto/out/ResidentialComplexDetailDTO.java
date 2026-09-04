package com.search_service.model.entityes.dto.out;

import com.search_service.model.entityes.Building;
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
    private List<Building> buildings;
}
