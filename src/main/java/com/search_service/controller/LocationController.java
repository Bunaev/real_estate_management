package com.search_service.controller;

import com.search_service.dto.out.LocationDTO;
import com.search_service.mapper.ReferenceMapper;
import com.search_service.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locations")
public class LocationController {
    private final LocationService locationService;
    private final ReferenceMapper referenceMapper;

    @GetMapping()
    public ResponseEntity<List<LocationDTO>> getLocations() {
        return ResponseEntity.ok(referenceMapper.toLocationDtoList(locationService.findAll()));
    }

    @PostMapping()
    public ResponseEntity<LocationDTO> createLocation(@RequestParam String name) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(referenceMapper.toLocationDto(locationService.create(name)));
    }
}