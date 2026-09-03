package com.search_service.model.services;

import com.search_service.model.entityes.*;
import com.search_service.model.entityes.dto.MetroDistanceDto;
import com.search_service.model.repo.DeveloperRepo;
import com.search_service.model.repo.DistrictRepo;
import com.search_service.model.repo.MetroStationRepo;
import com.search_service.model.repo.ResidentialComplexRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResidentialComplexService {
    private final ResidentialComplexRepo complexRepo;
    private final DistrictRepo districtRepo;
    private final DeveloperRepo developerRepo;
    private final MetroStationRepo metroRepo;

    @Transactional
    public ResidentialComplex create(String name, String address, Long districtId, Long developerId, List<MetroDistanceDto> metroDistances) {
        District district = districtRepo.findById(districtId).orElseThrow(() -> new RuntimeException("Района с id: " + districtId + " в базе не существует."));
        Developer developer = developerRepo.findById(developerId).orElseThrow(() -> new RuntimeException("Застройщика с id: " + developerId + " в базе не существует."));
        ResidentialComplex residentialComplex = ResidentialComplex.builder().name(name).address(address).district(district).developer(developer).build();
        if (residentialComplex.getMetroDistances() == null) {
            residentialComplex.setMetroDistances(new ArrayList());
        }

        for (MetroDistanceDto dto : metroDistances) {
            MetroStation metroStation = metroRepo.findById(dto.getMetroStationId()).orElseThrow(() -> new RuntimeException("Станции метро c id: " + dto.getMetroStationId() + " в базе не существует."));
            ComplexMetroDistance complexMetroDistance = ComplexMetroDistance.builder().metroStation(metroStation).residentialComplex(residentialComplex).distance(dto.getDistance()).build();
            residentialComplex.getMetroDistances().add(complexMetroDistance);
        }

        return complexRepo.save(residentialComplex);
    }

    public List<ResidentialComplex> findAll() {
        return complexRepo.findAll();
    }

    public void delete(Long id) {
        complexRepo.deleteById(id);
    }
}
