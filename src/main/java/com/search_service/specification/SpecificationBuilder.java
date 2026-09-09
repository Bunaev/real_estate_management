package com.search_service.specification;

import com.search_service.dto.in.FilterDTO;
import com.search_service.entity.Apartment;
import com.search_service.entity.Building;
import com.search_service.entity.ComplexMetroDistance;
import com.search_service.entity.Entrance;
import com.search_service.entity.ResidentialComplex;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class SpecificationBuilder {

    public Specification<Apartment> buildApartments(FilterDTO filter) {
        return (root, query, cb) -> {
            if (filter == null) return cb.conjunction();
            List<Predicate> predicates = new ArrayList<>();
            boolean needsDistinct = addApartmentContextPredicates(root, cb, predicates, filter);
            addApartmentLeafPredicates(root, cb, predicates, filter);
            if (needsDistinct) query.distinct(true);
            return and(predicates, cb);
        };
    }

    public Specification<ResidentialComplex> buildComplexes(FilterDTO filter) {
        return (root, query, cb) -> {
            if (filter == null) return cb.conjunction();
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getLocationId() != null) {
                predicates.add(cb.equal(root.get("district").get("location").get("id"), filter.getLocationId()));
            }
            if (filter.getDistrictId() != null) {
                predicates.add(cb.equal(root.get("district").get("id"), filter.getDistrictId()));
            }
            if (hasValues(filter.getDeveloperIds())) {
                predicates.add(root.get("developer").get("id").in(filter.getDeveloperIds()));
            } else if (filter.getDeveloperId() != null) {
                predicates.add(cb.equal(root.get("developer").get("id"), filter.getDeveloperId()));
            }
            if (filter.getResidentialComplexId() != null) {
                predicates.add(cb.equal(root.get("id"), filter.getResidentialComplexId()));
            }

            if (hasValues(filter.getMetroStationIds())) {
                Join<ResidentialComplex, ComplexMetroDistance> metro =
                        root.join("metroDistances", JoinType.LEFT);
                predicates.add(metro.get("metroStation").get("id").in(filter.getMetroStationIds()));
                query.distinct(true);
            }

            if (hasApartmentParams(filter)) {
                predicates.add(cb.exists(buildApartmentExistsSubquery(root, query, cb, filter)));
            }

            return and(predicates, cb);
        };
    }

    private static void addApartmentLeafPredicates(Root<Apartment> root, CriteriaBuilder cb,
                                                   List<Predicate> predicates, FilterDTO filter) {
        if (hasValues(filter.getTypes())) {
            predicates.add(root.get("type").in(filter.getTypes()));
        }
        if (filter.getBathroomType() != null) {
            predicates.add(cb.equal(root.get("bathroomType"), filter.getBathroomType()));
        }
        if (filter.getFloorFrom() != null || filter.getFloorTo() != null) {
            Path<Integer> floor = root.get("floor");
            if (filter.getFloorFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(floor, filter.getFloorFrom()));
            }
            if (filter.getFloorTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(floor, filter.getFloorTo()));
            }
        }
        if (filter.getAreaFrom() != null || filter.getAreaTo() != null) {
            Path<Double> area = root.get("area");
            if (filter.getAreaFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(area, filter.getAreaFrom()));
            }
            if (filter.getAreaTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(area, filter.getAreaTo()));
            }
        }
        if (filter.getPriceFrom() != null || filter.getPriceTo() != null) {
            Path<Double> price = root.get("price");
            if (filter.getPriceFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(price, filter.getPriceFrom()));
            }
            if (filter.getPriceTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(price, filter.getPriceTo()));
            }
        }
        if (filter.getHasBalcony() != null) {
            predicates.add(cb.equal(root.get("hasBalcony"), filter.getHasBalcony()));
        }
        if (filter.getStatus() != null) {
            predicates.add(cb.equal(root.get("status"), filter.getStatus()));
        }
    }

    private static boolean addApartmentContextPredicates(Root<Apartment> root, CriteriaBuilder cb,
                                                         List<Predicate> predicates, FilterDTO filter) {
        Join<Apartment, Entrance> entrance = null;
        Join<Entrance, Building> building = null;
        Join<Building, ResidentialComplex> complex = null;
        boolean needsDistinct = false;

        if (filter.getEntranceId() != null) {
            entrance = leftJoin(root, "entrance");
            predicates.add(cb.equal(entrance.get("id"), filter.getEntranceId()));
        }
        if (filter.getBuildingId() != null) {
            building = leftJoin(leftJoin(root, "entrance"), "building");
            predicates.add(cb.equal(building.get("id"), filter.getBuildingId()));
        }
        if (hasAny(filter, "residentialComplexId", "districtId", "locationId", "developerId", "metroStationIds")) {
            complex = leftJoin(leftJoin(leftJoin(root, "entrance"), "building"), "residentialComplex");
        }
        if (filter.getResidentialComplexId() != null) {
            predicates.add(cb.equal(complex.get("id"), filter.getResidentialComplexId()));
        }
        if (filter.getDistrictId() != null) {
            predicates.add(cb.equal(complex.get("district").get("id"), filter.getDistrictId()));
        }
        if (filter.getLocationId() != null) {
            predicates.add(cb.equal(complex.get("district").get("location").get("id"), filter.getLocationId()));
        }
        if (hasValues(filter.getDeveloperIds())) {
            predicates.add(complex.get("developer").get("id").in(filter.getDeveloperIds()));
            needsDistinct = true;
        } else if (filter.getDeveloperId() != null) {
            predicates.add(cb.equal(complex.get("developer").get("id"), filter.getDeveloperId()));
        }
        if (hasValues(filter.getMetroStationIds())) {
            Join<ResidentialComplex, ComplexMetroDistance> metro =
                    leftJoin(complex, "metroDistances");
            predicates.add(metro.get("metroStation").get("id").in(filter.getMetroStationIds()));
            needsDistinct = true;
        }
        return needsDistinct;
    }


    private static Subquery<Long> buildApartmentExistsSubquery(Root<ResidentialComplex> outerComplex,
                                                               CriteriaQuery<?> query, CriteriaBuilder cb,
                                                               FilterDTO filter) {
        Subquery<Long> subquery = query.subquery(Long.class);
        Root<Apartment> apartment = subquery.from(Apartment.class);
        subquery.select(apartment.get("id").as(Long.class));

        Join<Apartment, Entrance> entrance = leftJoin(apartment, "entrance");
        Join<Entrance, Building> building = leftJoin(entrance, "building");
        Join<Building, ResidentialComplex> complex = leftJoin(building, "residentialComplex");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(complex, outerComplex));
        addApartmentLeafPredicates(apartment, cb, predicates, filter);
        subquery.where(cb.and(predicates.toArray(new Predicate[0])));
        return subquery;
    }

    private static <P, T> Join<P, T> leftJoin(From<?, P> owner, String attribute) {
        return owner.join(attribute, JoinType.LEFT);
    }

    private static boolean hasApartmentParams(FilterDTO filter) {
        return hasValues(filter.getTypes())
                || filter.getBathroomType() != null
                || filter.getFloorFrom() != null || filter.getFloorTo() != null
                || filter.getAreaFrom() != null || filter.getAreaTo() != null
                || filter.getPriceFrom() != null || filter.getPriceTo() != null
                || filter.getHasBalcony() != null
                || filter.getStatus() != null;
    }

    private static boolean hasValues(List<?> values) {
        return values != null && !values.isEmpty();
    }

    private static boolean hasAny(FilterDTO filter, String... fields) {
        for (String field : fields) {
            switch (field) {
                case "residentialComplexId" -> { if (filter.getResidentialComplexId() != null) return true; }
                case "districtId" -> { if (filter.getDistrictId() != null) return true; }
                case "locationId" -> { if (filter.getLocationId() != null) return true; }
                case "developerId" -> { if (filter.getDeveloperId() != null || hasValues(filter.getDeveloperIds())) return true; }
                case "metroStationIds" -> { if (hasValues(filter.getMetroStationIds())) return true; }
                default -> { }
            }
        }
        return false;
    }

    private static Predicate and(List<Predicate> predicates, CriteriaBuilder cb) {
        if (predicates.isEmpty()) return cb.conjunction();
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
