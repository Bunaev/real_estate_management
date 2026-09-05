package com.search_service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDTO {
    private Long id;
    private Integer number;
    private Integer floor;
    private Double area;
    private Double price;
    private String type;
    private String bathroomType;
    private Boolean hasBalcony;
    private String status;
    private EntranceInfoDTO entrance;
}