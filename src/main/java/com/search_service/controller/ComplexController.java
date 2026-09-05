package com.search_service.controller;

import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.ResidentialComplexDetailDTO;
import com.search_service.dto.out.ResidentialComplexOutDTO;
import com.search_service.dto.out.ResidentialComplexShortDTO;
import com.search_service.service.ResidentialComplexService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/complexes")
public class ComplexController {
    private final ResidentialComplexService complexService;

    @GetMapping()
    public List<ResidentialComplexOutDTO> getComplexes() {
        return complexService.findAllLightweight();
    }

    @PostMapping
    public ResponseEntity<ResidentialComplexShortDTO> createComplex(@Valid @RequestBody ResidentialComplexDTO dto) {
        ResidentialComplexShortDTO created = complexService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComplex(@PathVariable Long id) {
        complexService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResidentialComplexDetailDTO> getComplex(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(complexService.findById(id));
    }
}