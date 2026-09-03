package com.search_service.model.services;

import com.search_service.model.entityes.Developer;
import com.search_service.model.repo.DeveloperRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeveloperService {
    private final DeveloperRepo devRepo;

    public Developer create(String name) {
        return devRepo.save(Developer.builder().name(name).build());
    }

    public List<Developer> findAll() {
        return devRepo.findAll();
    }
}
