package com.search_service.model.entityes.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResidentialComplexShortDTO {
    private Long id;
    private String name;
    private String developer;
    private String fullAddress;
}
