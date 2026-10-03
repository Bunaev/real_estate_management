package com.search_service.scheduled.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.search_service.entity.ScheduledTask;
import com.search_service.entity.TaskType;
import com.search_service.scheduled.tasks.PayloadChangePriceTask;
import com.search_service.scheduled.handlers.tools.TaskHandler;
import com.search_service.service.ApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChangePriceTaskHandler implements TaskHandler {

    private final ApartmentService service;
    private final ObjectMapper mapper;

    @Override
    public TaskType supports() {
        return TaskType.CHANGE_PRICE;
    }

    @Override
    public void handle(ScheduledTask task) {
        PayloadChangePriceTask payload;
        try {
            payload = mapper.readValue(task.getPayload(), PayloadChangePriceTask.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Не смог прочитать payload задачи " + task.getId(), e);
        }
        service.priceMatrix(payload.getFilterDTO(),
                payload.getTypeChange(),
                payload.getValue(),
                payload.getArithmeticalOperation());
    }
}
