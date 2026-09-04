package com.search_service.model.repo;

import com.search_service.model.entityes.Apartment;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentRepo extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {
    List<Apartment> findAllByEntrance_Id(Long entranceId);

    List<Apartment> findByEntrance_Building_ResidentialComplex_Id(Long complexId);
}
