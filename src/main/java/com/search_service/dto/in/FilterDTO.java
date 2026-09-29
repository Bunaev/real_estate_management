package com.search_service.dto.in;

import com.search_service.entity.ApartmentType;
import com.search_service.entity.BathroomType;
import com.search_service.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class FilterDTO {
    private Long locationId;
    private Long districtId;
    private Long developerId;
    private List<Long> developerIds;
    private Long residentialComplexId;
    private Long buildingId;
    private Long entranceId;
    private List<Long> metroStationIds;
    private List<ApartmentType> types;
    private BathroomType bathroomType;
    private Integer floorFrom;
    private Integer floorTo;
    private Double areaFrom;
    private Double areaTo;
    private BigDecimal priceFrom;
    private BigDecimal priceTo;
    private Boolean hasBalcony;
    private Status status;
}