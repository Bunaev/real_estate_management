package com.search_service.service;

import com.search_service.entity.Location;
import com.search_service.repository.LocationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepo locationRepo;

    @Transactional(readOnly = true)
    public List<Location> findAll() {
        return locationRepo.findAll();
    }

    @Transactional
    public Location create(String name) {
        return locationRepo.save(Location.builder().name(name).build());
    }
}