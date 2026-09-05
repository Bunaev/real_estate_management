package com.search_service.repository;

import com.search_service.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepo extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {
    List<Apartment> findAllByEntrance_Id(Long entranceId);

    List<Apartment> findByEntrance_Building_ResidentialComplex_Id(Long complexId);
}