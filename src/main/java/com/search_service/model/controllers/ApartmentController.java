package com.search_service.model.controllers;

import com.search_service.model.entityes.Apartment;
import com.search_service.model.entityes.dto.in.ApartmentInDTO;
import com.search_service.model.entityes.dto.out.ApartmentDTO;
import com.search_service.model.services.ApartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public List<ApartmentDTO> getApartments(@RequestParam Long complexId) {
        return apartmentService.findByComplexId(complexId);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApartmentDTO> updateApartment(
            @PathVariable Long id,
            @Valid @RequestBody ApartmentInDTO dto) {
        dto.setId(id);
        ApartmentDTO updated = apartmentService.update(dto);
        return ResponseEntity.ok(updated);
    }

}
