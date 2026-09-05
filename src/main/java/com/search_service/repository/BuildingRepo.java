package com.search_service.repository;

import com.search_service.entity.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepo extends JpaRepository<Building, Long> {
    List<Building> findByResidentialComplex_Id(Long residentialComplexId);
}