package com.search_service.repository;

import com.search_service.dto.out.NonPlanKeyDTO;
import com.search_service.entity.Apartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentRepo extends JpaRepository<Apartment, Long>, JpaSpecificationExecutor<Apartment> {
    List<Apartment> findAllByEntrance_Id(Long entranceId);

    List<Apartment> findByEntrance_Building_ResidentialComplex_Id(Long complexId);

    @Query("""
    SELECT new com.search_service.dto.out.NonPlanKeyDTO(
        c.id, c.name,
        b.id, b.name,
        e.id, e.name,
        COUNT(a.id)
    )
    FROM Apartment a
    JOIN a.entrance e
    JOIN e.building b
    JOIN b.residentialComplex c
    WHERE a.planKey IS NULL OR a.entrancePlanKey IS NULL
    GROUP BY c.id, c.name, b.id, b.name, e.id, e.name
    HAVING COUNT(a.id) > 0
    """)
    List<NonPlanKeyDTO> findMissingPlansSummary();
}