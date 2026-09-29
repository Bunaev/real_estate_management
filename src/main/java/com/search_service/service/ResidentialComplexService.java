package com.search_service.service;

import com.search_service.documents.ComplexDocument;
import com.search_service.dto.in.FilterDTO;
import com.search_service.dto.in.ResidentialComplexDTO;
import com.search_service.dto.out.*;
import com.search_service.entity.Developer;
import com.search_service.entity.District;
import com.search_service.entity.ResidentialComplex;
import com.search_service.exception.EntityNotFoundException;
import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
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

import java.io.IOException;
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
            try {
                String key = s3StorageService.uploadComplexPublicFile(saved.getId(), file.getBytes(), file.getOriginalFilename());
                saved.setKeyRenderPath(key);
            } catch (IOException exception) {
                throw new GeneralFormatException(TypeError.SAVE_FILE, "Ошибка получения массива байт: " + file.getOriginalFilename());
            }
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
            try {
                String key = s3StorageService.uploadComplexPublicFile(complex.getId(), file.getBytes(), file.getOriginalFilename());
                complex.setKeyRenderPath(key);
            } catch (IOException exception) {
                throw new GeneralFormatException(TypeError.SAVE_FILE, "Ошибка получения массива байт: " + file.getOriginalFilename());
            }
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

    @Transactional
    public String getDocumentPath(Long id) {
        ResidentialComplex complex = requireComplex(id);
        String path = complex.getKeyDocumentPath();
        if (path == null) {
            path = s3StorageService.generatePublicDocumentPath(id);
            complex.setKeyDocumentPath(path);
            complexRepo.save(complex);
        }
        return path;
    }

    @Transactional(readOnly = true)
    public String getPresentationPath(Long id) {
        ResidentialComplex complex = requireComplex(id);
        String key = complex.getKeyPresentationPath();
        if (key == null || key.endsWith("/") || !s3StorageService.doesPublicObjectExist(key)) {
            return null;
        }
        return s3StorageService.generatePublicDownloadUrl(key);
    }

    @Transactional
    public String getPresentationFolderPath(Long id) {
        requireComplex(id);
        return s3StorageService.generatePublicPresentationPath(id);
    }

    @Transactional
    public String putDocuments(String path, MultipartFile... files) {
        requireFolderPath(path, "documents");
        int count = 0;
        for (MultipartFile file : files) {
            uploadFile(path, file);
            count++;
        }
        return "По пути: " + path + " сохранено " + count + " файлов.";
    }

    @Transactional
    public String putPresentation(String path, MultipartFile file) {
        Long complexId = requireFolderPath(path, "presentation");
        ResidentialComplex complex = requireComplex(complexId);

        String oldKey = complex.getKeyPresentationPath();
        String newKey = uploadFile(path, file);
        complex.setKeyPresentationPath(newKey);
        complexRepo.save(complex);

        if (oldKey != null && !oldKey.endsWith("/") && !oldKey.equals(newKey)) {
            s3StorageService.deletePublicFile(oldKey);
        }
        return newKey;
    }

    @Transactional
    public void deleteDocument(String key) {
        requireFileKey(key, "documents");
        s3StorageService.deletePublicFile(key);
    }

    @Transactional
    public void deletePresentation(String key) {
        Long complexId = requireFileKey(key, "presentation");
        ResidentialComplex complex = requireComplex(complexId);

        s3StorageService.deletePublicFile(key);
        if (key.equals(complex.getKeyPresentationPath())) {
            complex.setKeyPresentationPath(null);
            complexRepo.save(complex);
        }
    }

    private String uploadFile(String path, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new GeneralFormatException(TypeError.FILE_IS_EMPTY, "Файл пустой: " + (file == null ? "null" : file.getOriginalFilename()));
        }
        try {
            return s3StorageService.uploadPublicFile(path, file.getBytes(), file.getContentType(), file.getOriginalFilename());
        } catch (IOException exception) {
            throw new GeneralFormatException(TypeError.SAVE_FILE, "Ошибка чтения файла: " + file.getOriginalFilename());
        }
    }

    private Long requireFolderPath(String path, String folder) {
        Long complexId = extractComplexId(path);
        if (complexId == null || !path.contains("/" + folder + "/") || !path.endsWith("/")) {
            throw new GeneralFormatException(TypeError.SAVE_FILE, "Некорректный путь к папке: " + path);
        }
        return complexId;
    }

    private Long requireFileKey(String key, String folder) {
        Long complexId = extractComplexId(key);
        if (complexId == null || !key.contains("/" + folder + "/") || key.endsWith("/")) {
            throw new GeneralFormatException(TypeError.SAVE_FILE, "Некорректный ключ файла: " + key);
        }
        return complexId;
    }

    private Long extractComplexId(String key) {
        if (key == null) return null;
        String[] parts = key.split("/");
        if (parts.length < 3 || !"complexes".equals(parts[0])) return null;
        try {
            return Long.valueOf(parts[1]);
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}