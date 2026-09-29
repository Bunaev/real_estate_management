package com.search_service.controller;

import com.search_service.dto.in.ApartmentInDTO;
import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.out.response.PriceMatrixResponse;
import com.search_service.entity.TypePriceChange;
import com.search_service.dto.out.ApartmentDTO;
import com.search_service.dto.out.NonPlanKeyDTO;
import com.search_service.service.ApartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    public ResponseEntity<Page<ApartmentDTO>> getApartments(
            @RequestParam Long complexId,
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        FilterDTO filter = FilterDTO.builder().residentialComplexId(complexId).build();
        return ResponseEntity.ok(apartmentService.getFilteredApartment(filter, pageable));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApartmentDTO> updateApartment(
            @PathVariable Long id,
            @RequestPart("dto") @Valid ApartmentInDTO dto,
            @RequestPart(value = "planFile", required = false) MultipartFile planFile,
            @RequestPart(value = "entrancePlanFile", required = false) MultipartFile entrancePlanFile) {
        dto.setId(id);
        ApartmentDTO updated = apartmentService.update(dto, planFile, entrancePlanFile);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<ApartmentDTO>> getFilteredApartment(
            FilterDTO filter,
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(apartmentService.getFilteredApartment(filter, pageable));
    }

    @GetMapping("/missing-plans-summary")
    public ResponseEntity<List<NonPlanKeyDTO>> checkPlanKeyAllApartments() {
        return ResponseEntity.status(HttpStatus.OK).body(apartmentService.checkPlanKeyAllApartments());
    }

    @PutMapping("/price-matrix")
    public ResponseEntity<PriceMatrixResponse> priceMatrixChanged(@RequestPart("filter") FilterDTO filterDTO,
                                                                  @RequestPart("type") TypePriceChange typeChange,
                                                                  @RequestPart("value") BigDecimal value) {;
        return ResponseEntity.status(HttpStatus.OK).body(apartmentService.priceMatrix(filterDTO, typeChange, value));
    }
}