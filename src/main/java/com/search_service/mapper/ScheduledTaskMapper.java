package com.search_service.mapper;

import com.search_service.dto.out.ScheduledTaskOutDto;
import com.search_service.entity.ScheduledTask;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduledTaskMapper {

    ScheduledTaskOutDto toDTO(ScheduledTask task);
}
