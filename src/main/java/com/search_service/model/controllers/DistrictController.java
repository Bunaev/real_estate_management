package com.search_service.model.controllers;

import com.search_service.model.entityes.District;
import com.search_service.model.services.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/disticts")
public class DistrictController {

    private final DistrictService districtService;

    @GetMapping("by-location/{locationId}")
    public List<District> getDistrictsByLocation(@PathVariable Long locationId) {
        return districtService.findByLocationId(locationId);
    }

    @PostMapping()
    public ResponseEntity<District> createDistrict(@RequestParam String name, @RequestParam Long locationId) {
        return ResponseEntity.ok(districtService.create(name, locationId));
    }
}
