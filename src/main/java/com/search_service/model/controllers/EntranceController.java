package com.search_service.model.controllers;

import com.search_service.model.entityes.Entrance;
import com.search_service.model.entityes.dto.in.EntranceDTO;
import com.search_service.model.services.EntranceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/entrances")
public class EntranceController {

    private final EntranceService entranceService;

    @PostMapping
    public ResponseEntity<Entrance> createEntrance(@RequestParam Long buildingId, @Valid @RequestBody EntranceDTO entranceDTO) {
        Entrance entrance = entranceService.create(buildingId, entranceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(entrance);
    }

    @GetMapping("by-building/{buildingId}")
    public List<Entrance> getEntrancesByBuilding(@PathVariable Long buildingId) {
        return entranceService.findByBuildingId(buildingId);
    }
}
