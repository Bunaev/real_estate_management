package com.search_service.controller;

import com.search_service.dto.in.EntranceDTO;
import com.search_service.dto.out.EntranceShortDTO;
import com.search_service.entity.Entrance;
import com.search_service.mapper.EntranceMapper;
import com.search_service.service.EntranceService;
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
    public ResponseEntity<EntranceShortDTO> createEntrance(@RequestParam Long buildingId, @Valid @RequestBody EntranceDTO entranceDTO) {
        EntranceShortDTO entrance = entranceService.create(buildingId, entranceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(entrance);
    }

    @GetMapping("by-building/{buildingId}")
    public List<EntranceShortDTO> getEntrancesByBuilding(@PathVariable Long buildingId) {
        return entranceService.findByBuildingId(buildingId);
    }
}