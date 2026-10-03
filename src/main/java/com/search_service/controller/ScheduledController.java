package com.search_service.controller;

import com.search_service.dto.in.SchedulePriceChangeRequestDTO;
import com.search_service.dto.out.ScheduledTaskOutDto;
import com.search_service.service.ScheduledService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/scheduled-tasks")
public class ScheduledController {

    private final ScheduledService service;

    @PostMapping("/price-change")
    public ScheduledTaskOutDto schedulePriceChange(@RequestBody @Valid SchedulePriceChangeRequestDTO request) {
        return service.schedulePriceChange(request);
    }
}
