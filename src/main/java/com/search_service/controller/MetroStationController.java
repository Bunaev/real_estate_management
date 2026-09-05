package com.search_service.controller;

import com.search_service.dto.out.MetroStationDTO;
import com.search_service.mapper.ReferenceMapper;
import com.search_service.service.MetroService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/metro-stations")
public class MetroStationController {
    private final MetroService metroService;
    private final ReferenceMapper referenceMapper;

    @GetMapping()
    public List<MetroStationDTO> getMetroStations() {
        return referenceMapper.toMetroStationDtoList(metroService.findAll());
    }

    @PostMapping()
    public ResponseEntity<MetroStationDTO> createMetroStation(@RequestParam String name) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(referenceMapper.toMetroStationDto(metroService.create(name)));
    }
}