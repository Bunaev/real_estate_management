package com.search_service.service;

import com.search_service.entity.MetroStation;
import com.search_service.repository.MetroStationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetroService {
    private final MetroStationRepo metroRepo;

    @Transactional(readOnly = true)
    public List<MetroStation> findAll() {
        return metroRepo.findAll();
    }

    @Transactional
    public MetroStation create(String name) {
        return metroRepo.save(MetroStation.builder().name(name).build());
    }
}