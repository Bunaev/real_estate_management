package com.search_service.model.services;

import com.search_service.model.entityes.Location;
import com.search_service.model.repo.LocationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepo locationRepo;

    public List<Location> findAll() {
        return locationRepo.findAll();
    }

    public Location create(String name) {
        return locationRepo.save(Location.builder().name(name).build());
    }

}
