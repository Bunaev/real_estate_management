package com.search_service.model.controllers;

import com.search_service.model.entityes.MetroStation;
import com.search_service.model.services.MetroService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/metro-stations")
public class MetroStationController {
    private final MetroService metroService;

    @GetMapping()
    public List<MetroStation> getMetroStations() {
        return metroService.findAll();
    }
}
