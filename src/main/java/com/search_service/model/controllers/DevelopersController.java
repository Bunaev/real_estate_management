package com.search_service.model.controllers;

import com.search_service.model.entityes.Developer;
import com.search_service.model.services.DeveloperService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/developers")
public class DevelopersController {
    private final DeveloperService developerService;

    @GetMapping()
    public List<Developer> getDevelopers() {
        return developerService.findAll();
    }

    @PostMapping()
    public ResponseEntity<Developer> createDeveloper(@RequestParam String name) {
        return ResponseEntity.ok(developerService.create(name));
    }
}
