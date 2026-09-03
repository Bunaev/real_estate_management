package com.search_service.model.services;

import com.search_service.model.entityes.MetroStation;
import com.search_service.model.repo.MetroStationRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetroService {
    private final MetroStationRepo metroRepo;

    public List<MetroStation> findAll() {
        return metroRepo.findAll();
    }

}
