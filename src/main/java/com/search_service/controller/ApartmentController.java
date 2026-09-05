package com.search_service.controller;

import com.search_service.dto.in.ApartmentInDTO;
import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.out.ApartmentDTO;
import com.search_service.service.ApartmentService;
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
@RequestMapping("/api/apartments")
public class ApartmentController {

    private final ApartmentService apartmentService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApartment(@PathVariable Long id) {
        apartmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<ApartmentDTO>> getApartments(
            @RequestParam Long complexId,
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        FilterDTO filter = FilterDTO.builder().residentialComplexId(complexId).build();
        return ResponseEntity.ok(apartmentService.getFilteredApartment(filter, pageable));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartmentDTO> updateApartment(
            @PathVariable Long id,
            @Valid @RequestBody ApartmentInDTO dto) {
        dto.setId(id);
        ApartmentDTO updated = apartmentService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ApartmentDTO>> getFilteredApartment(
            FilterDTO filter,
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(apartmentService.getFilteredApartment(filter, pageable));
    }
}