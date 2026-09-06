package com.search_service.controller;

import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.ResidentialComplexDetailDTO;
import com.search_service.dto.out.ResidentialComplexEditDTO;
import com.search_service.dto.out.ResidentialComplexOutDTO;
import com.search_service.dto.out.ResidentialComplexShortDTO;
import com.search_service.service.ResidentialComplexService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/complexes")
public class ComplexController {
    private final ResidentialComplexService complexService;

    @GetMapping()
    public Page<ResidentialComplexOutDTO> getComplexes(
            FilterDTO filter,
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return complexService.findAllLightweight(filter, pageable);
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

    @GetMapping("/{id}/edit")
    public ResponseEntity<ResidentialComplexEditDTO> getComplexForEdit(@PathVariable Long id) {
        ResidentialComplexEditDTO dto = complexService.findForEdit(id);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResidentialComplexOutDTO> update(@Valid @RequestBody ResidentialComplexDTO dto, @PathVariable Long id) {
        ResidentialComplexOutDTO updated = complexService.update(dto);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
}