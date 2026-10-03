package com.search_service.scheduled.handlers.tools;

import com.search_service.entity.ScheduledTask;
import com.search_service.entity.TaskStatus;
import com.search_service.repository.ScheduledTaskRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScheduledTaskProcessor {

    private final ScheduledTaskRepo taskRepo;
    private final TaskHandlerRegistry registry;

    @Scheduled(fixedDelay = 60_000)
    public void processDueTasks() {
        List<ScheduledTask> due = taskRepo.findScheduledTasks(LocalDateTime.now());

        for (ScheduledTask task : due) {
            try {
                registry.get(task.getType()).handle(task);
                task.setStatus(TaskStatus.COMPLETED);
            } catch (Exception e) {
                log.error("Task {} failed", task.getId(), e);
                task.setStatus(TaskStatus.ERROR);
            }
            taskRepo.save(task);
        }
    }
}
