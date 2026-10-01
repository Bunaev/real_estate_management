package com.search_service.service;

import com.search_service.dto.in.ApartmentInDTO;
import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.out.ApartmentDTO;
import com.search_service.dto.out.NonPlanKeyDTO;
import com.search_service.dto.out.response.PriceMatrixResponse;
import com.search_service.entity.Apartment;
import com.search_service.entity.Entrance;
import com.search_service.entity.Status;
import com.search_service.entity.TypePriceChange;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.ApartmentMapper;
import com.search_service.repository.ApartmentRepo;
import com.search_service.repository.EntranceRepo;
import com.search_service.specification.SpecificationBuilder;
import com.search_service.util.ExcelUtils;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApartmentService {
    private final ApartmentRepo apartmentRepo;
    private final EntranceRepo entranceRepo;
    private final ApartmentMapper mapper;
    private final SpecificationBuilder specificationBuilder;
    private final ApartmentRenderService renderService;

    @Transactional
    public List<Apartment> importApartments(Long entranceId, MultipartFile file) {
        Entrance entrance = entranceRepo.findById(entranceId)
                .orElseThrow(() -> new EntityNotFoundException("Секция", entranceId));
        List<Apartment> apartments = ExcelUtils.readExcelFile(file, Apartment.class);
        for (Apartment apartment : apartments) {
            apartment.setEntrance(entrance);
            if (apartment.getPrice() != null) {
                apartment.setPrice(apartment.getPrice().setScale(2, RoundingMode.HALF_UP));
            }
            if (apartment.getPricePerSquareMeter() != null) {
                apartment.setPricePerSquareMeter(apartment.getPricePerSquareMeter().setScale(2, RoundingMode.HALF_UP));
            }
            if (apartment.getArea() == null){
                apartment.setArea(Math.round((apartment.getKitchenArea() == null ? 0.0 : apartment.getKitchenArea() +
                        apartment.getBathroomArea() + apartment.getHallwayArea() + apartment.getRoomsArea()) * 100) / 100.0);
            } else if (apartment.getPricePerSquareMeter() == null) {
                apartment.setPricePerSquareMeter(apartment.getPrice().divide(
                        BigDecimal.valueOf(apartment.getArea()), 2, RoundingMode.HALF_UP));
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
    public ApartmentDTO update(ApartmentInDTO dto, MultipartFile planFile, MultipartFile entrancePlanFile) {
        Apartment apartment = requireApartment(dto.getId());

        String oldPlanKey = apartment.getPlanKey();
        String oldEntrancePlanKey = apartment.getEntrancePlanKey();
        Double area = resolveArea(dto);

        updateMainFields(apartment, dto);
        updateAreaFields(apartment, dto, area);
        updatePriceFields(apartment, dto, area);
        updateNotes(apartment, dto);
        renderService.updateRenders(apartment, dto.getPlanKey(), dto.getEntrancePlanKey(), planFile, entrancePlanFile);

        Apartment saved = apartmentRepo.save(apartment);
        renderService.deleteReplacedRenders(
                oldPlanKey, oldEntrancePlanKey,
                saved.getPlanKey(), saved.getEntrancePlanKey()
        );
        return mapper.toDto(saved);
    }

    private Apartment requireApartment(Long id) {
        return apartmentRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Квартира", id));
    }

    private void updateMainFields(Apartment apartment, ApartmentInDTO dto) {
        apartment.setFloor(dto.getFloor());
        apartment.setNumber(dto.getNumber());
        apartment.setHasBalcony(dto.getHasBalcony());
        apartment.setStatus(dto.getStatus());
        apartment.setType(dto.getType());
        apartment.setBathroomType(dto.getBathroomType());
    }

    private void updateAreaFields(Apartment apartment, ApartmentInDTO dto, Double area) {
        apartment.setKitchenArea(dto.getKitchenArea());
        apartment.setHallwayArea(dto.getHallwayArea());
        apartment.setBathroomArea(dto.getBathroomArea());
        apartment.setRoomsArea(dto.getRoomsArea());
        apartment.setArea(area);
    }

    private Double resolveArea(ApartmentInDTO dto) {
        if (dto.getArea() != null) {
            return dto.getArea();
        }
        double sum = (dto.getKitchenArea() == null ? 0.0 : dto.getKitchenArea())
                + dto.getHallwayArea()
                + dto.getBathroomArea()
                + dto.getRoomsArea();
        return Math.round(sum * 100) / 100.0;
    }

    private void updatePriceFields(Apartment apartment, ApartmentInDTO dto, Double area) {
        apartment.setPrice(dto.getPrice().setScale(2, RoundingMode.HALF_UP));
        apartment.setPricePerSquareMeter(
                dto.getPricePerSquareMeter() != null
                        ? dto.getPricePerSquareMeter().setScale(2, RoundingMode.HALF_UP)
                        : dto.getPrice().divide(BigDecimal.valueOf(area), 2, RoundingMode.HALF_UP)
        );
    }

    private void updateNotes(Apartment apartment, ApartmentInDTO dto) {
        apartment.setNotes(dto.getNotes());
    }

    @Transactional(readOnly = true)
    public Page<ApartmentDTO> getFilteredApartment(FilterDTO filter, Pageable pageable) {
        Specification<Apartment> specification = specificationBuilder.buildApartments(filter);
        return apartmentRepo.findAll(specification, pageable).map(mapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<NonPlanKeyDTO> checkPlanKeyAllApartments() {
        return apartmentRepo.findMissingPlansSummary();
    }

    @Transactional
    public List<PriceMatrixResponse> priceMatrix(FilterDTO filter,
                                                 TypePriceChange typeChange,
                                                 BigDecimal value,
                                                 Boolean arithmeticalOperation) {
        FilterDTO copyFilter = filter.toBuilder().status(Status.AVAILABLE).build();
        Specification<Apartment> specification = specificationBuilder.buildApartments(copyFilter);
        List<Apartment> apartments = apartmentRepo.findAll(specification);
        List<PriceMatrixResponse> result = new ArrayList<>();
        for (Apartment apartment : apartments) {
            Integer numberApartment = apartment.getNumber();
            BigDecimal oldPrice = apartment.getPrice();
            changePriceForMatrix(typeChange, value, arithmeticalOperation, apartment);
            BigDecimal newPrice = apartment.getPrice();
            result.add(PriceMatrixResponse.builder().numberApartment(numberApartment).oldPrice(oldPrice).newPrice(newPrice).build());
        }
        apartmentRepo.saveAll(apartments);
        return result;
    }

    private static void changePriceForMatrix(TypePriceChange typeChange,
                                             BigDecimal value,
                                             boolean increase,
                                             Apartment apartment) {
        BigDecimal signedValue = increase ? value : value.negate();
        BigDecimal area = BigDecimal.valueOf(apartment.getArea());

        switch (typeChange) {
            case ABSOLUTE_PRICE -> {
                BigDecimal newPricePerSqm = value.setScale(2, RoundingMode.HALF_UP);
                apartment.setPricePerSquareMeter(newPricePerSqm);
                apartment.setPrice(newPricePerSqm.multiply(area).setScale(2, RoundingMode.HALF_UP));
            }
            case PERCENT -> {
                BigDecimal percentValue = signedValue.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                BigDecimal newPricePerSqm = apartment.getPricePerSquareMeter()
                        .multiply(BigDecimal.ONE.add(percentValue))
                        .setScale(2, RoundingMode.HALF_UP);
                apartment.setPricePerSquareMeter(newPricePerSqm);
                apartment.setPrice(newPricePerSqm.multiply(area).setScale(2, RoundingMode.HALF_UP));
            }
            case FIXED_PER_SQM -> {
                BigDecimal newPricePerSqm = apartment.getPricePerSquareMeter()
                        .add(signedValue)
                        .setScale(2, RoundingMode.HALF_UP);
                apartment.setPricePerSquareMeter(newPricePerSqm);
                apartment.setPrice(newPricePerSqm.multiply(area).setScale(2, RoundingMode.HALF_UP));
            }
        }
    }
}
