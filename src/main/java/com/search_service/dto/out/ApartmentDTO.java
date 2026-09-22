package com.search_service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentDTO {
    private Long id;
    private Integer number;
    private Integer floor;
    private Double area;
    private Double kitchenArea;
    private Double hallwayArea;
    private Double bathroomArea;
    private Double roomsArea;
    private Double price;
    private String type;
    private String bathroomType;
    private Boolean hasBalcony;
    private String status;
    private String planKey;
    private String entrancePlanKey;
    private EntranceInfoDTO entrance;
    private LocalDate completionDate;
    private LocalDate keyHandoverDate;
}