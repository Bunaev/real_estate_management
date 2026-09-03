package com.search_service.model.services;

import com.search_service.model.entityes.District;
import com.search_service.model.entityes.Location;
import com.search_service.model.repo.DistrictRepo;
import com.search_service.model.repo.LocationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictService {
    private final DistrictRepo districtRepo;
    private final LocationRepo locationRepo;

    public List<District> findAll() {
        return districtRepo.findAll();
    }

    public District create(String name, Long locationId) {
        Location location = this.locationRepo.findById(locationId).orElseThrow(() -> new RuntimeException("Такой локации не существует."));
        return districtRepo.save(District.builder().name(name).location(location).build());
    }

    public List<District> findByLocationId(Long locationId) {
        return districtRepo.findByLocation_Id(locationId);
    }
}