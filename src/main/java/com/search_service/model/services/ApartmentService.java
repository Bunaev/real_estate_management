package com.search_service.model.services;

import com.search_service.model.entityes.Apartment;
import com.search_service.model.entityes.Entrance;
import com.search_service.model.repo.ApartmentRepo;
import com.search_service.model.repo.EntranceRepo;
import com.search_service.model.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentService {
    public final ApartmentRepo apartmentRepo;
    private final EntranceRepo entranceRepo;

    public List<Apartment> importApartments(Long entranceId, MultipartFile file) {
        Entrance entrance = this.entranceRepo.findById(entranceId).orElseThrow(() -> new RuntimeException("Такой секции не существует."));
        List<Apartment> apartments = ExcelUtils.readExcelFile(file, Apartment.class);

        for (Apartment apartment : apartments) {
            apartment.setEntrance(entrance);
        }

        return apartmentRepo.saveAll(apartments);
    }

    public byte[] exportApartmentByEntranceId(Long entranceId) {
        List<Apartment> apartments = apartmentRepo.findAllByEntrance_Id(entranceId);
        return ExcelUtils.exportEntityToExcel(apartments, Apartment.class);
    }
}
