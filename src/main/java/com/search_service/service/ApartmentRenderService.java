package com.search_service.service;

import com.search_service.dto.in.ApartmentImportDTO;
import com.search_service.dto.out.response.ApartmentImportResponse;
import com.search_service.dto.out.response.GeneralImportResponse;
import com.search_service.dto.out.response.ImportStatus;
import com.search_service.entity.Apartment;
import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import com.search_service.repository.ApartmentRepo;
import com.search_service.util.ZipUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApartmentRenderService {

    private static final int PLAN_TYPES_COUNT = ApartmentPlanType.values().length;

    private final ApartmentRepo apartmentRepo;
    private final S3StorageService storageService;

    public GeneralImportResponse importRenders(MultipartFile archive, List<ApartmentImportDTO> apartments) {
        if (apartments == null || apartments.isEmpty()) {
            return GeneralImportResponse.of(List.of());
        }
        Map<String, byte[]> images = ZipUtils.readImages(archive);
        List<ApartmentImportResponse> result = apartments.stream()
                .map(apartment -> importApartmentRenders(apartment, images))
                .toList();
        return GeneralImportResponse.of(result);
    }

    private ApartmentImportResponse importApartmentRenders(ApartmentImportDTO dto, Map<String, byte[]> images) {
        if (dto.getId() == null || dto.getNumber() == null) {
            log.warn("Планировки: в запросе не указан id или номер квартиры");
            return toResponse(dto, ImportStatus.ERROR, "Не указан id или номер квартиры");
        }
        Apartment apartment = apartmentRepo.findById(dto.getId()).orElse(null);
        if (apartment == null) {
            log.warn("Планировки: квартира {} не найдена", dto.getId());
            return toResponse(dto, ImportStatus.ERROR, "Квартира не найдена");
        }
        try {
            String oldPlanKey = apartment.getPlanKey();
            String oldEntrancePlanKey = apartment.getEntrancePlanKey();
            int uploaded = uploadRenders(apartment, dto.getNumber(), images);
            if (uploaded > 0) {
                Apartment saved = apartmentRepo.save(apartment);
                deleteReplacedRenders(oldPlanKey, oldEntrancePlanKey,
                        saved.getPlanKey(), saved.getEntrancePlanKey());
            }
            ImportStatus status = resolveStatus(uploaded);
            log.info("Планировки: квартира {}, загружено {} из {}", dto.getId(), uploaded, PLAN_TYPES_COUNT);
            return toResponse(dto, status, statusMessage(dto.getNumber(), uploaded, status));
        } catch (RuntimeException exception) {
            log.error("Планировки: ошибка загрузки для квартиры {}", dto.getId(), exception);
            return toResponse(dto, ImportStatus.ERROR, failureMessage(exception));
        }
    }

    private int uploadRenders(Apartment apartment, Integer apartmentNumber, Map<String, byte[]> images) {
        int uploaded = 0;
        for (ApartmentPlanType planType : ApartmentPlanType.values()) {
            String fileName = planType.resolveFileName(apartmentNumber);
            byte[] content = images.get(fileName);
            if (content == null) {
                continue;
            }
            String key = storageService.uploadApartmentPublicFile(apartment.getId(), content, fileName);
            applyRenderKey(apartment, key, planType);
            uploaded++;
        }
        return uploaded;
    }

    public void updateRenders(Apartment apartment, String planKey, String entrancePlanKey,
                              MultipartFile planFile, MultipartFile entrancePlanFile) {
        String newPlanKey = resolveRenderKey(apartment, planKey, planFile);
        String newEntrancePlanKey = resolveRenderKey(apartment, entrancePlanKey, entrancePlanFile);

        applyRenderKey(apartment, newPlanKey, ApartmentPlanType.PLAN);
        applyRenderKey(apartment, newEntrancePlanKey, ApartmentPlanType.ENTRANCE_PLAN);
    }

    public void deleteReplacedRenders(String oldPlanKey, String oldEntrancePlanKey,
                                      String newPlanKey, String newEntrancePlanKey) {
        deleteReplacedRender(oldPlanKey, newPlanKey);
        deleteReplacedRender(oldEntrancePlanKey, newEntrancePlanKey);
    }

    private String resolveRenderKey(Apartment apartment, String dtoKey, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return uploadRender(apartment, file);
        }
        return dtoKey == null || dtoKey.isBlank() ? null : dtoKey.trim();
    }

    private String uploadRender(Apartment apartment, MultipartFile file) {
        try {
            return storageService.uploadApartmentPublicFile(apartment.getId(), file.getBytes(), file.getOriginalFilename());
        } catch (IOException exception) {
            throw new GeneralFormatException(TypeError.FILE_READ, "Ошибка чтения файла планировки: " + file.getOriginalFilename());
        }
    }

    private void applyRenderKey(Apartment apartment, String newKey, ApartmentPlanType planType) {
        planType.applyKey(apartment, newKey);
    }

    private void deleteReplacedRender(String oldKey, String newKey) {
        if (oldKey == null || oldKey.isBlank() || Objects.equals(oldKey, newKey)) {
            return;
        }
        storageService.deleteObject(storageService.getPublicBucket(), oldKey);
        log.debug("Удалён старый рендер: {}", oldKey);
    }

    private ImportStatus resolveStatus(int uploaded) {
        if (uploaded == 0) {
            return ImportStatus.FILE_NOT_FOUND;
        }
        return uploaded == PLAN_TYPES_COUNT ? ImportStatus.OK : ImportStatus.PARTIAL;
    }

    private String statusMessage(Integer apartmentNumber, int uploaded, ImportStatus status) {
        return switch (status) {
            case OK -> "Загружены все планировки (%d из %d)".formatted(uploaded, PLAN_TYPES_COUNT);
            case PARTIAL -> "Загружена только часть планировок (%d из %d). Ожидались файлы: %s"
                    .formatted(uploaded, PLAN_TYPES_COUNT, expectedFileNames(apartmentNumber));
            default -> "Файлы планировок не найдены в архиве. Ожидались файлы: " + expectedFileNames(apartmentNumber);
        };
    }

    private String expectedFileNames(Integer apartmentNumber) {
        return Arrays.stream(ApartmentPlanType.values())
                .map(planType -> planType.resolveFileName(apartmentNumber))
                .collect(Collectors.joining(", "));
    }

    private String failureMessage(RuntimeException exception) {
        return exception.getMessage() == null ? "Не удалось загрузить планировки" : exception.getMessage();
    }

    private ApartmentImportResponse toResponse(ApartmentImportDTO dto, ImportStatus status, String message) {
        return ApartmentImportResponse.builder()
                .id(dto.getId())
                .number(dto.getNumber())
                .status(status)
                .message(message)
                .build();
    }
}