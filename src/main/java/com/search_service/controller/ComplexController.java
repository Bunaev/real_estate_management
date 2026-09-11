package com.search_service.controller;

import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.*;
import com.search_service.service.ResidentialComplexService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/complexes")
public class ComplexController {

    private final ResidentialComplexService complexService;

    @GetMapping()
    public Page<ResidentialComplexEditDTO> getComplexes(
            FilterDTO filter,
            @PageableDefault(size = 50, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return complexService.findAllLightweight(filter, pageable);
    }

    @GetMapping("{id}/render")
    public ResponseEntity<Resource> getImageRender(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType("image/jpg")).body(complexService.getImageRender(id));
    }

    @GetMapping("/suggest")
    public List<SearchSuggestionDTO> suggest(@RequestParam("q") String query,
                                             @RequestParam(defaultValue = "5") int limit) {
        return complexService.suggest(query, limit);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResidentialComplexShortDTO> createComplex(@RequestPart("dto") @Valid ResidentialComplexDTO dto,
                                                                    @RequestPart(value = "file", required = false) MultipartFile file) {
        ResidentialComplexShortDTO created = complexService.create(dto, file);
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

    @PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResidentialComplexOutDTO> update(@RequestPart("dto") @Valid ResidentialComplexDTO dto,
                                                           @RequestPart(value = "file", required = false) MultipartFile file) {
        ResidentialComplexOutDTO updated = complexService.update(dto, file);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }
//    curl -X DELETE "localhost:9200/complexes"

}