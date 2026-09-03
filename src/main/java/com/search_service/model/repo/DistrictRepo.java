package com.search_service.model.repo;

import com.search_service.model.entityes.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepo extends JpaRepository<District, Long> {
    List<District> findByLocation_Id(Long locationId);
}