package com.search_service.service;

import com.search_service.dto.in.ApartmentInDTO;
import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.out.ApartmentDTO;
import com.search_service.entity.Apartment;
import com.search_service.entity.Entrance;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.ApartmentMapper;
import com.search_service.repository.ApartmentRepo;
import com.search_service.repository.EntranceRepo;
import com.search_service.specification.SpecificationBuilder;
import com.search_service.util.ExcelUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApartmentService {
    private final ApartmentRepo apartmentRepo;
    private final EntranceRepo entranceRepo;
    private final ApartmentMapper mapper;
    private final SpecificationBuilder specificationBuilder;

    @Transactional
    public List<Apartment> importApartments(Long entranceId, MultipartFile file) {
        Entrance entrance = entranceRepo.findById(entranceId)
                .orElseThrow(() -> new EntityNotFoundException("Секция", entranceId));
        List<Apartment> apartments = ExcelUtils.readExcelFile(file, Apartment.class);
        for (Apartment apartment : apartments) {
            apartment.setEntrance(entrance);
            // Price = area * pricePerSquareMeter (if set), otherwise keep existing price
            if (apartment.getPricePerSquareMeter() != null && apartment.getArea() != null) {
                apartment.setPrice(apartment.getArea() * apartment.getPricePerSquareMeter());
            }
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
        return mapper.toDtoList(apartmentRepo.findByEntrance_Building_ResidentialComplex_Id(complexId));
    }

    @Transactional
    public void delete(Long id) {
        if (!apartmentRepo.existsById(id)) {
            throw new EntityNotFoundException("Квартира", id);
        }
        apartmentRepo.deleteById(id);
    }

    @Transactional
    public ApartmentDTO update(ApartmentInDTO dto) {
        Apartment apartment = apartmentRepo.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Квартира", dto.getId()));
        apartment.setArea(dto.getArea());
        apartment.setFloor(dto.getFloor());
        apartment.setNumber(dto.getNumber());
        apartment.setHasBalcony(dto.getHasBalcony());
        apartment.setStatus(dto.getStatus());
        apartment.setType(dto.getType());
        // Price = area * pricePerSquareMeter; if pricePerSquareMeter not set, keep old price
        if (dto.getPricePerSquareMeter() != null) {
            apartment.setPricePerSquareMeter(dto.getPricePerSquareMeter());
            apartment.setPrice(apartment.getArea() * dto.getPricePerSquareMeter());
        } else {
            apartment.setPrice(dto.getPrice());
        }
        apartment.setBathroomType(dto.getBathroomType());
        return mapper.toDto(apartmentRepo.save(apartment));
    }

    @Transactional(readOnly = true)
    public Page<ApartmentDTO> getFilteredApartment(FilterDTO filter, Pageable pageable) {
        Specification<Apartment> specification = specificationBuilder.buildApartments(filter);
        return apartmentRepo.findAll(specification, pageable).map(mapper::toDto);
    }
}


