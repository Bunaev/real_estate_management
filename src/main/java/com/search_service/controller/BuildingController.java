package com.search_service.controller;

import com.search_service.dto.in.BuildingDTO;
import com.search_service.dto.out.BuildingShortDTO;
import com.search_service.entity.Building;
import com.search_service.mapper.BuildingMapper;
import com.search_service.service.BuildingService;
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
    private final BuildingMapper buildingMapper;

    @GetMapping("by-complex/{complexId}")
    public List<BuildingShortDTO> getBuildingsByComplex(@PathVariable Long complexId) {
        return buildingMapper.toListDTO(buildingService.findByComplexId(complexId));
    }

    @PostMapping
    public ResponseEntity<BuildingShortDTO> createBuilding(
            @RequestParam Long residentialComplexId,
            @Valid @RequestBody BuildingDTO buildingDTO) {
        Building building = buildingService.create(buildingDTO, residentialComplexId);
        return ResponseEntity.status(HttpStatus.CREATED).body(buildingMapper.toListDTO(List.of(building)).get(0));
    }
}