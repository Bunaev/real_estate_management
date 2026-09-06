package com.search_service.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentialComplexEditDTO {
    private Long id;
    private String name;
    private String address;

    private Long locationId;
    private Long districtId;
    private Long developerId;

    private String locationName;
    private String districtName;
    private String developerName;

    private List<MetroDistanceEditDTO> metroDistances;

    private List<BuildingEditDTO> buildings;


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MetroDistanceEditDTO {
        private Long metroStationId;
        private String stationName;
        private Integer distance;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BuildingEditDTO {
        private Long id;
        private String name;
        private LocalDate completionDate;
        private LocalDate keyHandoverDate;
        private List<EntranceEditDTO> entrances;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntranceEditDTO {
        private Long id;
        private String name;
        private Integer maxFloor;
    }
}
