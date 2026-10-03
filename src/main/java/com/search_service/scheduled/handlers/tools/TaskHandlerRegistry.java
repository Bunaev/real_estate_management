package com.search_service.scheduled.handlers.tools;

import com.search_service.entity.TaskType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class TaskHandlerRegistry {

    private final Map<TaskType, TaskHandler> handlers;

    public TaskHandlerRegistry(List<TaskHandler> allHandlers) {
        this.handlers = allHandlers.stream()
                .collect(Collectors.toMap(TaskHandler::supports, Function.identity()));
    }

    public TaskHandler get(TaskType type) {
        TaskHandler handler = handlers.get(type);
        if (handler == null) {
            throw new IllegalStateException("Нет handler для типа " + type);
        }
        return handler;
    }
}
