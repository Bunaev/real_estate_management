package com.search_service.model.controllers;

import com.search_service.model.entityes.Location;
import com.search_service.model.services.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {
    private final LocationService locationService;

    @GetMapping()
    public ResponseEntity<List<Location>> getLocations() {
        return ResponseEntity.ok(locationService.findAll());
    }

    @PostMapping()
    public ResponseEntity<Location> createLocation(@RequestParam String name) {
        return ResponseEntity.ok(locationService.create(name));
    }

}
