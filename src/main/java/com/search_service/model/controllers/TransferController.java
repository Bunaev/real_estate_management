package com.search_service.model.controllers;

import com.search_service.model.entityes.Apartment;
import com.search_service.model.services.ApartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TransferController {

    private final ApartmentService apartmentService;

    @PostMapping(value = "/import-apartments", consumes = "multipart/form-data")
    public ResponseEntity<String> importApartments(@RequestParam("entranceId") Long entranceId, @RequestPart("file") MultipartFile file) {
        try {
            List<Apartment> apartments = apartmentService.importApartments(entranceId, file);
            return ResponseEntity.ok("Загружено " + apartments.size() + " квартир");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Ошибка: " + e.getMessage());
        }
    }

    @GetMapping("/export-apartments/{entranceId}")
    public ResponseEntity<byte[]> exportApartments(@PathVariable Long entranceId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "apartments.xlsx");
        return ResponseEntity.ok().headers(headers).body(apartmentService.exportApartmentByEntranceId(entranceId));
    }
}
