package com.search_service.dto.out.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentImportResponse {
    private Long id;
    private Integer number;
    private ImportStatus status;
    private String message;
}
