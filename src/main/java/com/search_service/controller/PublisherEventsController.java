package com.search_service.controller;

import com.search_service.dto.in.PublishedEventDTO;
import com.search_service.entity.PublishedEvent;
import com.search_service.service.PublishedEventsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/publisher")
public class PublisherEventsController {

    private final PublishedEventsService service;

    @GetMapping()
    public ResponseEntity<List<PublishedEvent>> getEvents() {
        return ResponseEntity.status(HttpStatus.OK).body(service.getAllActualEvents());
    }

    @PostMapping()
    public ResponseEntity<PublishedEvent> createEvent(@RequestBody PublishedEventDTO event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createEvent(event));
    }

}
