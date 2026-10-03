package com.search_service.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search_service.dto.in.SchedulePriceChangeRequestDTO;
import com.search_service.dto.out.ScheduledTaskOutDto;
import com.search_service.entity.PublishedEvent;
import com.search_service.entity.ScheduledTask;
import com.search_service.entity.TaskStatus;
import com.search_service.entity.TaskType;
import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import com.search_service.mapper.ScheduledTaskMapper;
import com.search_service.repository.ScheduledTaskRepo;
import com.search_service.scheduled.tasks.PayloadChangePriceTask;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ScheduledService {

    private final ScheduledTaskRepo taskRepo;
    private final ObjectMapper objectMapper;
    private final ScheduledTaskMapper mapper;

    public ScheduledTaskOutDto schedulePriceChange(SchedulePriceChangeRequestDTO request) {
        PayloadChangePriceTask payload = PayloadChangePriceTask.builder()
                .filterDTO(request.getFilter())
                .value(request.getValue())
                .typeChange(request.getTypeChange())
                .arithmeticalOperation(request.getArithmeticalOperation()).build();

        String payloadJson;
        try {
            payloadJson = objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new GeneralFormatException(TypeError.SAVE_FILE, "Не удалось сериализовать payload");
        }
        ScheduledTask task = ScheduledTask.builder()
                .type(TaskType.CHANGE_PRICE)
                .deadLine(request.getDeadLine())
                .status(TaskStatus.PLANNED)
                .userId(0L)
                .publishNews(request.isPublishNews())
                .payload(payloadJson)
                .created(LocalDateTime.now())
                .build();

        return mapper.toDTO(taskRepo.save(task));
    }


}

