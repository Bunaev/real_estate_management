package com.search_service.model.controllers;

import com.search_service.model.entityes.Building;
import com.search_service.model.entityes.dto.in.BuildingDTO;
import com.search_service.model.services.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buildings")
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("by-complex/{complexId}")
    public List<Building> getBuildingsByComplex(@PathVariable Long complexId) {
        return buildingService.findByComplexId(complexId);
    }

    @PostMapping
    public ResponseEntity<Building> createBuilding(
            @RequestParam Long residentialComplexId,
            @Valid @RequestBody BuildingDTO buildingDTO) {
        Building building = buildingService.create(buildingDTO, residentialComplexId);
        return ResponseEntity.status(HttpStatus.CREATED).body(building);
    }
}
