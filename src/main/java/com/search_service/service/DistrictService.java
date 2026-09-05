package com.search_service.service;

import com.search_service.entity.District;
import com.search_service.entity.Location;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.repository.DistrictRepo;
import com.search_service.repository.LocationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DistrictService {
    private final DistrictRepo districtRepo;
    private final LocationRepo locationRepo;

    @Transactional(readOnly = true)
    public List<District> findAll() {
        return districtRepo.findAll();
    }

    @Transactional
    public District create(String name, Long locationId) {
        Location location = locationRepo.findById(locationId)
                .orElseThrow(() -> new EntityNotFoundException("Локация", locationId));
        return districtRepo.save(District.builder().name(name).location(location).build());
    }

    @Transactional(readOnly = true)
    public List<District> findByLocationId(Long locationId) {
        return districtRepo.findByLocation_Id(locationId);
    }
}