package com.search_service.dto.in;

import com.search_service.entity.TaskType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PublishedEventDTO {

    @NotNull(message = "Тип публикации обязателен")
    private TaskType type;

    private String targetName;
    private String content;

    @NotNull(message = "Дата начала обязательна")
    private LocalDate startEventDate;
    @NotNull(message = "Дата окончания обязательна")
    @Future(message = "Дата окончания публикации должна быть в будущем")
    private LocalDate endEventDate;
    private String eventPictureKey;
}
