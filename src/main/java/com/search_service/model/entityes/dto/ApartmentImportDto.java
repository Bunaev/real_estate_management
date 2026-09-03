package com.search_service.model.entityes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.search_service.model.entityes.BathroomType;
import com.search_service.model.entityes.Status;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class ApartmentImportDto {
    private Integer number;
    private Integer floor;
    private Double area;
    private Double price;
    private Boolean hasBalcony;
    private Status status;
    private BathroomType bathroomType;

}

