package com.search_service.specification;

import com.search_service.dto.in.FilterDTO;
import com.search_service.entity.*;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SpecificationBuilder {

    public Specification<Apartment> build(FilterDTO filter) {
        List<Specification<Apartment>> specs = new ArrayList<>();

        specs.add(byLocationId(filter.getLocationId()));
        specs.add(byDistrictId(filter.getDistrictId()));
        specs.add(byDeveloperId(filter.getDeveloperId()));
        specs.add(byResidentialComplexId(filter.getResidentialComplexId()));
        specs.add(byBuildingId(filter.getBuildingId()));
        specs.add(byEntranceId(filter.getEntranceId()));
        specs.add(byMetroStationIds(filter.getMetroStationIds()));
        specs.add(byTypes(filter.getTypes()));
        specs.add(byBathroomType(filter.getBathroomType()));
        specs.add(byFloorRange(filter.getFloorFrom(), filter.getFloorTo()));
        specs.add(byAreaRange(filter.getAreaFrom(), filter.getAreaTo()));
        specs.add(byPriceRange(filter.getPriceFrom(), filter.getPriceTo()));
        specs.add(byHasBalcony(filter.getHasBalcony()));
        specs.add(byStatus(filter.getStatus()));

        Specification<Apartment> result = Specification.where(null);
        for (Specification<Apartment> spec : specs) {
            if (spec != null) {
                result = result.and(spec);
            }
        }
        return result;
    }

    /**
     * Общий метод для JOIN-а всей цепочки: Apartment → Entrance → Building → ResidentialComplex.
     * Используется фильтрами, которым нужен ResidentialComplex.
     */
    private Join<Apartment, Entrance> joinEntrance(Root<Apartment> root) {
        return root.join("entrance", JoinType.LEFT);
    }

    private Join<Entrance, Building> joinBuilding(Join<Apartment, Entrance> entranceJoin) {
        return entranceJoin.join("building", JoinType.LEFT);
    }

    private Join<Building, ResidentialComplex> joinComplex(Join<Entrance, Building> buildingJoin) {
        return buildingJoin.join("residentialComplex", JoinType.LEFT);
    }

    private Specification<Apartment> byLocationId(Long locationId) {
        return (root, query, cb) -> {
            if (locationId == null) return cb.conjunction();
            Join<Apartment, Entrance> entrance = joinEntrance(root);
            Join<Entrance, Building> building = joinBuilding(entrance);
            Join<Building, ResidentialComplex> complex = joinComplex(building);
            Join<ResidentialComplex, District> district = complex.join("district", JoinType.LEFT);
            query.distinct(true);
            return cb.equal(district.get("location").get("id"), locationId);
        };
    }

    private Specification<Apartment> byDistrictId(Long districtId) {
        return (root, query, cb) -> {
            if (districtId == null) return cb.conjunction();
            Join<Apartment, Entrance> entrance = joinEntrance(root);
            Join<Entrance, Building> building = joinBuilding(entrance);
            Join<Building, ResidentialComplex> complex = joinComplex(building);
            query.distinct(true);
            return cb.equal(complex.get("district").get("id"), districtId);
        };
    }

    private Specification<Apartment> byDeveloperId(Long developerId) {
        return (root, query, cb) -> {
            if (developerId == null) return cb.conjunction();
            Join<Apartment, Entrance> entrance = joinEntrance(root);
            Join<Entrance, Building> building = joinBuilding(entrance);
            Join<Building, ResidentialComplex> complex = joinComplex(building);
            query.distinct(true);
            return cb.equal(complex.get("developer").get("id"), developerId);
        };
    }

    private Specification<Apartment> byResidentialComplexId(Long residentialComplexId) {
        return (root, query, cb) -> {
            if (residentialComplexId == null) return cb.conjunction();
            Join<Apartment, Entrance> entrance = joinEntrance(root);
            Join<Entrance, Building> building = joinBuilding(entrance);
            query.distinct(true);
            return cb.equal(building.get("residentialComplex").get("id"), residentialComplexId);
        };
    }

    private Specification<Apartment> byBuildingId(Long buildingId) {
        return (root, query, cb) -> {
            if (buildingId == null) return cb.conjunction();
            Join<Apartment, Entrance> entrance = joinEntrance(root);
            return cb.equal(entrance.get("building").get("id"), buildingId);
        };
    }

    private Specification<Apartment> byEntranceId(Long entranceId) {
        return (root, query, cb) -> {
            if (entranceId == null) return cb.conjunction();
            return cb.equal(root.get("entrance").get("id"), entranceId);
        };
    }

    private Specification<Apartment> byMetroStationIds(List<Long> metroStationIds) {
        return (root, query, cb) -> {
            if (metroStationIds == null || metroStationIds.isEmpty()) return cb.conjunction();
            Join<Apartment, Entrance> entrance = joinEntrance(root);
            Join<Entrance, Building> building = joinBuilding(entrance);
            Join<Building, ResidentialComplex> complex = joinComplex(building);
            Join<ResidentialComplex, ComplexMetroDistance> metroDist = complex.join("metroDistances", JoinType.LEFT);
            query.distinct(true);
            return metroDist.get("metroStation").get("id").in(metroStationIds);
        };
    }

    private Specification<Apartment> byTypes(List<ApartmentType> types) {
        return (root, query, cb) -> {
            if (types == null || types.isEmpty()) return cb.conjunction();
            CriteriaBuilder.In<Object> in = cb.in(root.get("type"));
            for (ApartmentType type : types) {
                in.value(type);
            }
            return in;
        };
    }

    private Specification<Apartment> byBathroomType(BathroomType bathroomType) {
        return (root, query, cb) -> {
            if (bathroomType == null) return cb.conjunction();
            return cb.equal(root.get("bathroomType"), bathroomType);
        };
    }

    private Specification<Apartment> byFloorRange(Integer floorFrom, Integer floorTo) {
        return (root, query, cb) -> {
            Path<Integer> floorPath = root.get("floor");
            Predicate predicate = cb.conjunction();
            if (floorFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(floorPath, floorFrom));
            }
            if (floorTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(floorPath, floorTo));
            }
            return predicate;
        };
    }

    private Specification<Apartment> byAreaRange(Double areaFrom, Double areaTo) {
        return (root, query, cb) -> {
            Path<Double> areaPath = root.get("area");
            Predicate predicate = cb.conjunction();
            if (areaFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(areaPath, areaFrom));
            }
            if (areaTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(areaPath, areaTo));
            }
            return predicate;
        };
    }

    private Specification<Apartment> byPriceRange(Double priceFrom, Double priceTo) {
        return (root, query, cb) -> {
            Path<Double> pricePath = root.get("price");
            Predicate predicate = cb.conjunction();
            if (priceFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(pricePath, priceFrom));
            }
            if (priceTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(pricePath, priceTo));
            }
            return predicate;
        };
    }

    private Specification<Apartment> byHasBalcony(Boolean hasBalcony) {
        return (root, query, cb) -> {
            if (hasBalcony == null) return cb.conjunction();
            return cb.equal(root.get("hasBalcony"), hasBalcony);
        };
    }

    private Specification<Apartment> byStatus(Status status) {
        return (root, query, cb) -> {
            if (status == null) return cb.conjunction();
            return cb.equal(root.get("status"), status);
        };
    }
}