package com.search_service.model.repo;

import com.search_service.model.entityes.ResidentialComplex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ResidentialComplexRepo extends JpaRepository<ResidentialComplex, Long>, JpaSpecificationExecutor<ResidentialComplex> {
}
