package com.search_service.repository;

import com.search_service.entity.ResidentialComplex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResidentialComplexRepo extends JpaRepository<ResidentialComplex, Long>, JpaSpecificationExecutor<ResidentialComplex> {

    Page<ResidentialComplex> findAll(Specification<ResidentialComplex> spec, Pageable pageable);

    @Query("""
                SELECT md.residentialComplex.id, md.metroStation.name, md.distance
                FROM ComplexMetroDistance md
                WHERE md.residentialComplex.id IN :ids
            """)
    List<Object[]> findMetroDistancesByComplexIds(@Param("ids") List<Long> ids);
}