package com.search_service.model.services;

import com.search_service.model.entityes.Apartment;
import com.search_service.model.entityes.Entrance;
import com.search_service.model.entityes.dto.ApartmentMapper;
import com.search_service.model.entityes.dto.in.ApartmentInDTO;
import com.search_service.model.entityes.dto.out.ApartmentDTO;
import com.search_service.model.repo.ApartmentRepo;
import com.search_service.model.repo.EntranceRepo;
import com.search_service.model.utils.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentService {
    public final ApartmentRepo apartmentRepo;
    private final EntranceRepo entranceRepo;
    private final ApartmentMapper mapper;

    @Transactional
    public List<Apartment> importApartments(Long entranceId, MultipartFile file) {
        Entrance entrance = this.entranceRepo.findById(entranceId).orElseThrow(() -> new RuntimeException("Такой секции не существует."));
        List<Apartment> apartments = ExcelUtils.readExcelFile(file, Apartment.class);

        for (Apartment apartment : apartments) {
            apartment.setEntrance(entrance);
        }

        return apartmentRepo.saveAll(apartments);
    }

    @Transactional(readOnly = true)
    public byte[] exportApartmentByEntranceId(Long entranceId) {
        List<Apartment> apartments = apartmentRepo.findAllByEntrance_Id(entranceId);
        return ExcelUtils.exportEntityToExcel(apartments, Apartment.class);
    }

    @Transactional(readOnly = true)
    public List<ApartmentDTO> findByComplexId(Long complexId) {
        return mapper.toListDtoList(apartmentRepo.findByEntrance_Building_ResidentialComplex_Id(complexId));
    }

    @Transactional
    public void delete(Long id) {
        apartmentRepo.deleteById(id);
    }

    @Transactional
    public ApartmentDTO update(ApartmentInDTO dto) {
        Apartment apartment = apartmentRepo.findById(dto.getId()).orElseThrow(() -> new RuntimeException("Квартиры не найдено!"));
        apartment.setArea(dto.getArea());
        apartment.setFloor(dto.getFloor());
        apartment.setNumber(dto.getNumber());
        apartment.setHasBalcony(dto.getHasBalcony());
        apartment.setStatus(dto.getStatus());
        apartment.setType(dto.getType());
        apartment.setPrice(dto.getPrice());
        apartment.setBathroomType(dto.getBathroomType());
        return mapper.toDto(apartmentRepo.save(apartment));
    }
}
