package com.search_service.model.repo;

import com.search_service.model.entityes.Building;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BuildingRepo extends JpaRepository<Building, Long> {
    List<Building> findByResidentialComplex_Id(Long residentialComplexId);
}