package com.search_service.service;

import com.search_service.dto.in.PublishedEventDTO;
import com.search_service.entity.PublishedEvent;
import com.search_service.repository.PublishedEventsRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PublishedEventsService {

    private final PublishedEventsRepo eventsRepo;

    private static final DateTimeFormatter RU_DATE =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");


    public List<PublishedEvent> getAllActualEvents() {
        return eventsRepo.findActiveEvents(LocalDate.now());
    }

    public PublishedEvent createEvent(PublishedEventDTO dto) {
        String content = formatContent(dto);

        PublishedEvent event = PublishedEvent.builder()
                .type(dto.getType())
                .eventContent(content)
                .startEventDate(dto.getStartEventDate())
                .endEventDate(dto.getEndEventDate())
                .eventPictureKey(dto.getEventPictureKey())
                .build();

        return eventsRepo.save(event);
    }

    private String formatContent(PublishedEventDTO dto) {
        return switch (dto.getType()) {
            case CHANGE_PRICE -> dto.getType().getTemplate().formatted(
                    dto.getStartEventDate().format(RU_DATE),
                    dto.getType().getDisplayName().toLowerCase(),
                    dto.getTargetName()
            );
            case START_OF_SALES -> dto.getType().getTemplate().formatted(
                    dto.getStartEventDate().format(RU_DATE),
                    dto.getTargetName()
            );
            case NEWS -> dto.getContent();
        };
    }
}
