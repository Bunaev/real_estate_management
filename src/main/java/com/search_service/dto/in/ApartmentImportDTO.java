package com.search_service.dto.in;

// Облегченная DTO-шка для импорта рендеров планировок. Валидировать смысла не вижу

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApartmentImportDTO {
    private Long id;
    private Integer number;
}
