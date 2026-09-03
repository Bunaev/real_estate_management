package com.search_service.model.services;

import com.search_service.model.entityes.Building;
import com.search_service.model.entityes.ResidentialComplex;
import com.search_service.model.repo.BuildingRepo;
import com.search_service.model.repo.ResidentialComplexRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {
    private final BuildingRepo buildingRepo;
    private final ResidentialComplexRepo complexRepo;

    public Building create(String name, Long residentialComplexId, LocalDate completionDate, LocalDate keyHandoverDate) {
        ResidentialComplex complex = this.complexRepo.findById(residentialComplexId).orElseThrow(() -> new RuntimeException("ЖК с указанным id не существует."));
        Building building = Building.builder().name(name).completionDate(completionDate).keyHandoverDate(keyHandoverDate).residentialComplex(complex).build();
        return buildingRepo.save(building);
    }

    public List<Building> findByComplexId(Long complexId) {
        return buildingRepo.findByResidentialComplex_Id(complexId);
    }
}
