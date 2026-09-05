package com.search_service.controller;

import com.search_service.dto.out.DistrictDTO;
import com.search_service.mapper.ReferenceMapper;
import com.search_service.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/districts")
public class DistrictController {

    private final DistrictService districtService;
    private final ReferenceMapper referenceMapper;

    @GetMapping("by-location/{locationId}")
    public List<DistrictDTO> getDistrictsByLocation(@PathVariable Long locationId) {
        return referenceMapper.toDistrictDtoList(districtService.findByLocationId(locationId));
    }

    @PostMapping()
    public ResponseEntity<DistrictDTO> createDistrict(@RequestParam String name, @RequestParam Long locationId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(referenceMapper.toDistrictDto(districtService.create(name, locationId)));
    }
}