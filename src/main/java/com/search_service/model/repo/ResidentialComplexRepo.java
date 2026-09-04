package com.search_service.model.repo;

import com.search_service.model.entityes.ResidentialComplex;
import com.search_service.model.entityes.dto.out.ResidentialComplexOutDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentialComplexRepo extends JpaRepository<ResidentialComplex, Long>, JpaSpecificationExecutor<ResidentialComplex> {

    @Query("""
    SELECT new com.search_service.model.entityes.dto.out.ResidentialComplexOutDTO(
        rc.id,
        rc.name,
        dev.name,
        rc.address,
        l.name,
        d.name,
        CAST((SELECT COUNT(b) FROM Building b WHERE b.residentialComplex = rc) AS int),
        CAST((SELECT COUNT(e) FROM Entrance e WHERE e.building.residentialComplex = rc) AS int),
        CAST((SELECT COUNT(a) FROM Apartment a WHERE a.entrance.building.residentialComplex = rc) AS int)
    )
    FROM ResidentialComplex rc
    LEFT JOIN rc.district d
    LEFT JOIN d.location l
    LEFT JOIN rc.developer dev
    ORDER BY rc.id
""")
    List<ResidentialComplexOutDTO> findAllLightweight();

    @Query("""
    SELECT md.residentialComplex.id, md.metroStation.name, md.distance
    FROM ComplexMetroDistance md
    WHERE md.residentialComplex.id IN :ids
""")
    List<Object[]> findMetroDistancesByComplexIds(@Param("ids") List<Long> ids);

}
