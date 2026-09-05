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
public class ResidentialComplexSpecificationBuilder {

    public Specification<ResidentialComplex> build(FilterDTO filter) {
        List<Specification<ResidentialComplex>> specs = new ArrayList<>();

        specs.add(byLocationId(filter.getLocationId()));
        specs.add(byDistrictId(filter.getDistrictId()));
        specs.add(byDeveloperId(filter.getDeveloperId()));
        specs.add(byMetroStationIds(filter.getMetroStationIds()));
        specs.add(byHasApartmentsWithFilter(filter)); // ← ключевой метод

        Specification<ResidentialComplex> result = Specification.where(null);
        for (Specification<ResidentialComplex> spec : specs) {
            if (spec != null) {
                result = result.and(spec);
            }
        }
        return result;
    }

    private Specification<ResidentialComplex> byLocationId(Long locationId) {
        return (root, query, cb) -> {
            if (locationId == null) return cb.conjunction();
            return cb.equal(root.get("district").get("location").get("id"), locationId);
        };
    }

    private Specification<ResidentialComplex> byDistrictId(Long districtId) {
        return (root, query, cb) -> {
            if (districtId == null) return cb.conjunction();
            return cb.equal(root.get("district").get("id"), districtId);
        };
    }

    private Specification<ResidentialComplex> byDeveloperId(Long developerId) {
        return (root, query, cb) -> {
            if (developerId == null) return cb.conjunction();
            return cb.equal(root.get("developer").get("id"), developerId);
        };
    }

    private Specification<ResidentialComplex> byMetroStationIds(List<Long> metroStationIds) {
        return (root, query, cb) -> {
            if (metroStationIds == null || metroStationIds.isEmpty()) return cb.conjunction();
            Join<ResidentialComplex, ComplexMetroDistance> metroJoin = root.join("metroDistances", JoinType.LEFT);
            query.distinct(true);
            return metroJoin.get("metroStation").get("id").in(metroStationIds);
        };
    }

    /**
     * 🔥 ГЛАВНЫЙ МЕТОД: фильтр по квартирным параметрам.
     * Использует подзапрос EXISTS, чтобы найти ЖК, в которых есть хотя бы одна квартира,
     * удовлетворяющая условиям фильтра.
     */
    private Specification<ResidentialComplex> byHasApartmentsWithFilter(FilterDTO filter) {
        return (root, query, cb) -> {
            // Если никаких квартирных фильтров нет — пропускаем
            if (filter.getTypes() == null && filter.getBathroomType() == null &&
                    filter.getFloorFrom() == null && filter.getFloorTo() == null &&
                    filter.getAreaFrom() == null && filter.getAreaTo() == null &&
                    filter.getPriceFrom() == null && filter.getPriceTo() == null &&
                    filter.getHasBalcony() == null && filter.getStatus() == null) {
                return cb.conjunction();
            }

            // Подзапрос: существуют ли квартиры в этом ЖК с нужными параметрами.
            // ВАЖНО: селект по id квартиры, а не COUNT — SELECT EXISTS(SELECT COUNT(*)...)
            // всегда возвращает одну строку (даже при 0 совпадений), и фильтр бы не работал.
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Apartment> apartmentRoot = subquery.from(Apartment.class);
            subquery.select(apartmentRoot.get("id").as(Long.class));

            // Связь: Apartment → Entrance → Building → ResidentialComplex
            Join<Apartment, Entrance> entranceJoin = apartmentRoot.join("entrance");
            Join<Entrance, Building> buildingJoin = entranceJoin.join("building");
            Join<Building, ResidentialComplex> complexJoin = buildingJoin.join("residentialComplex");

            // Условие: ЖК из основного запроса = ЖК из подзапроса
            Predicate sameComplex = cb.equal(complexJoin, root);

            // Условия по квартирам
            List<Predicate> apartmentPredicates = new ArrayList<>();
            apartmentPredicates.add(sameComplex);

            // Типы квартир (список)
            if (filter.getTypes() != null && !filter.getTypes().isEmpty()) {
                apartmentPredicates.add(apartmentRoot.get("type").in(filter.getTypes()));
            }

            // Тип санузла
            if (filter.getBathroomType() != null) {
                apartmentPredicates.add(cb.equal(apartmentRoot.get("bathroomType"), filter.getBathroomType()));
            }

            // Этажи (от-до)
            if (filter.getFloorFrom() != null) {
                apartmentPredicates.add(cb.greaterThanOrEqualTo(apartmentRoot.get("floor"), filter.getFloorFrom()));
            }
            if (filter.getFloorTo() != null) {
                apartmentPredicates.add(cb.lessThanOrEqualTo(apartmentRoot.get("floor"), filter.getFloorTo()));
            }

            // Площадь (от-до)
            if (filter.getAreaFrom() != null) {
                apartmentPredicates.add(cb.greaterThanOrEqualTo(apartmentRoot.get("area"), filter.getAreaFrom()));
            }
            if (filter.getAreaTo() != null) {
                apartmentPredicates.add(cb.lessThanOrEqualTo(apartmentRoot.get("area"), filter.getAreaTo()));
            }

            // Цена (от-до)
            if (filter.getPriceFrom() != null) {
                apartmentPredicates.add(cb.greaterThanOrEqualTo(apartmentRoot.get("price"), filter.getPriceFrom()));
            }
            if (filter.getPriceTo() != null) {
                apartmentPredicates.add(cb.lessThanOrEqualTo(apartmentRoot.get("price"), filter.getPriceTo()));
            }

            // Балкон
            if (filter.getHasBalcony() != null) {
                apartmentPredicates.add(cb.equal(apartmentRoot.get("hasBalcony"), filter.getHasBalcony()));
            }

            // Статус
            if (filter.getStatus() != null) {
                apartmentPredicates.add(cb.equal(apartmentRoot.get("status"), filter.getStatus()));
            }

            subquery.where(cb.and(apartmentPredicates.toArray(new Predicate[0])));

            // EXISTS (подзапрос вернул > 0)
            return cb.exists(subquery);
        };
    }
}