package com.search_service.scheduled.handlers.tools;

import com.search_service.entity.ScheduledTask;
import com.search_service.entity.TaskType;

public interface TaskHandler {
    TaskType supports();
    void handle(ScheduledTask task);
}
