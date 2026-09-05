package com.search_service.service;

import com.search_service.entity.Developer;
import com.search_service.repository.DeveloperRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeveloperService {
    private final DeveloperRepo devRepo;

    @Transactional
    public Developer create(String name) {
        return devRepo.save(Developer.builder().name(name).build());
    }

    @Transactional(readOnly = true)
    public List<Developer> findAll() {
        return devRepo.findAll();
    }
}