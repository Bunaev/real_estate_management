package com.search_service.service;

import com.search_service.documents.ComplexDocument;
import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.*;
import com.search_service.entity.Developer;
import com.search_service.entity.District;
import com.search_service.entity.ResidentialComplex;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.mapper.ResidentialComplexMapper;
import com.search_service.repository.DeveloperRepo;
import com.search_service.repository.DistrictRepo;
import com.search_service.repository.ResidentialComplexRepo;
import com.search_service.specification.SpecificationBuilder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResidentialComplexService {

    private final ResidentialComplexRepo complexRepo;
    private final DistrictRepo districtRepo;
    private final DeveloperRepo developerRepo;
    private final ResidentialComplexMapper mapper;
    private final SpecificationBuilder specificationBuilder;
    private final S3StorageService s3StorageService;
    private final ComplexSearchService complexSearchService;
    private final ResidentialComplexRelationService relationService;

    @Transactional
    public ResidentialComplexShortDTO create(ResidentialComplexDTO dto, MultipartFile file) {
        ResidentialComplex complex = mapper.toEntity(dto);

        complex.setDistrict(requireDistrict(dto));
        complex.setDeveloper(requireDeveloper(dto));
        complex.setLatitude(dto.getLatitude());
        complex.setLongitude(dto.getLongitude());

        relationService.applyRelations(complex, dto);

        ResidentialComplex saved = complexRepo.save(complex);

        if (file != null && !file.isEmpty()) {
            String key = s3StorageService.uploadPublicFile(saved.getId(), file);
            saved.setKeyRenderPath(key);
        }
        complexSearchService.indexComplex(saved);
        return mapper.toShortDto(saved);
    }

    @Transactional(readOnly = true)
    public Page<ComplexCardOutDTO> findAllLightweight(FilterDTO filter, Pageable page) {
        Specification<ResidentialComplex> specification = specificationBuilder.buildComplexes(filter);
        Page<ResidentialComplex> complexPage = complexRepo.findAll(specification, page);

        List<ComplexCardOutDTO> dtoList = complexPage.getContent().stream()
                .map(mapper::toCardOutDto)
                .collect(Collectors.toList());

        enrichWithMetro(dtoList);
        return new PageImpl<>(dtoList, complexPage.getPageable(), complexPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ResidentialComplexDetailDTO findById(Long id) {
        return mapper.toDetailDto(requireComplex(id));
    }

    @Transactional(readOnly = true)
    public ResidentialComplexEditDTO findForEdit(Long id) {
        ResidentialComplex complex = complexRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ЖК не найден"));
        return mapper.toEditDto(complex);
    }


    @Transactional
    public ResidentialComplexShortDTO update(@Valid ResidentialComplexDTO dto, MultipartFile file) {
        ResidentialComplex complex = requireComplex(dto.getId());

        complex.setName(dto.getName());
        complex.setAddress(dto.getAddress());
        complex.setDistrict(requireDistrict(dto));
        complex.setDeveloper(requireDeveloper(dto));

        if (file != null && !file.isEmpty()) {
            String key = s3StorageService.uploadPublicFile(complex.getId(), file);
            complex.setKeyRenderPath(key);
        }

        relationService.updateRelations(complex, dto);

        ResidentialComplex saved = complexRepo.save(complex);
        complexSearchService.indexComplex(saved);
        return mapper.toShortDto(saved);
    }


    @Transactional
    public void delete(Long id) {
        if (!complexRepo.existsById(id)) {
            throw new EntityNotFoundException("ЖК", id);
        }
        String key = complexRepo.findRenderKeyById(id).orElseThrow(() -> new EntityNotFoundException("ЖК", id));
        complexSearchService.deleteComplex(id);
        complexRepo.deleteById(id);
        s3StorageService.deleteObject(s3StorageService.getPublicBucket(), key);
    }


    @Transactional(readOnly = true)
    public void reindexAllComplex() {
        complexSearchService.reindexAll(complexRepo.findAll());
    }

    public List<ComplexDocument> fuzzySearch(String query, int from, int size) {
        return complexSearchService.fuzzySearch(query, from, size);
    }

    public List<SearchSuggestionDTO> suggest(String query, int limit) {
        return complexSearchService.suggest(query, limit);
    }


    private ResidentialComplex requireComplex(Long id) {
        return complexRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ЖК", id));
    }

    private District requireDistrict(ResidentialComplexDTO dto) {
        return districtRepo.findById(dto.getDistrictId()).orElseThrow();
    }

    private Developer requireDeveloper(ResidentialComplexDTO dto) {
        return developerRepo.findById(dto.getDeveloperId()).orElseThrow();
    }

    private void enrichWithMetro(List<ComplexCardOutDTO> dtoList) {
        List<Long> ids = dtoList.stream()
                .map(ComplexCardOutDTO::getId)
                .toList();
        if (ids.isEmpty()) return;

        List<Object[]> metroData = complexRepo.findMetroDistancesByComplexIds(ids);
        Map<Long, List<MetroDistanceOutDTO>> metroMap = metroData.stream()
                .collect(Collectors.groupingBy(
                        row -> (Long) row[0],
                        Collectors.mapping(
                                row -> MetroDistanceOutDTO.builder()
                                        .stationName((String) row[1])
                                        .distance((Integer) row[2])
                                        .build(),
                                Collectors.toList()
                        )
                ));

        dtoList.forEach(dto -> dto.setMetroDistances(metroMap.getOrDefault(dto.getId(), List.of())));
    }
}