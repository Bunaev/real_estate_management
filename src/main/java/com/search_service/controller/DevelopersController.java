package com.search_service.controller;

import com.search_service.dto.out.DeveloperDTO;
import com.search_service.mapper.ReferenceMapper;
import com.search_service.service.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/developers")
public class DevelopersController {
    private final DeveloperService developerService;
    private final ReferenceMapper referenceMapper;

    @GetMapping()
    public List<DeveloperDTO> getDevelopers() {
        return referenceMapper.toDeveloperDtoList(developerService.findAll());
    }

    @PostMapping()
    public ResponseEntity<DeveloperDTO> createDeveloper(@RequestParam String name) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(referenceMapper.toDeveloperDto(developerService.create(name)));
    }
}