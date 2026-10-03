package com.search_service.dto.out;

import com.search_service.entity.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledTaskOutDto {
    private Long id;
    private LocalDateTime deadline;
    private TaskType type;
    private String payload;
}
