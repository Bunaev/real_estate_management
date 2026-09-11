package com.search_service.service;

import com.search_service.entity.ResidentialComplex;
import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * Сервис файлового хранилища.
 * Отвечает только за сохранение/удаление/чтение файлов и ничего не знает о БД.
 */
@Service
@Slf4j
public class FileStorageService {

    @Value("${storage.path}")
    private String storageRoot;

    /**
     * Сохраняет рендер/изображение ЖК и возвращает относительный путь для поля renderPath.
     * Если файл пустой — возвращает null.
     */
    public String saveComplexRender(Long complexId, String complexName, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            Path complexDir = Paths.get(storageRoot, "complexes", String.valueOf(complexId));
            Files.createDirectories(complexDir);

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new GeneralFormatException(TypeError.SAVE_FILE, "Файл без расширения");
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            String safeName = (complexName == null ? "complex" : complexName)
                    .replaceAll("[\\\\/:*?\"<>|]", "")
                    .replaceAll("\\s+", "_")
                    .trim();
            String filename = safeName + extension;

            Files.write(complexDir.resolve(filename), file.getBytes());
            return "complexes/" + complexId + "/" + filename;
        } catch (GeneralFormatException e) {
            throw e;
        } catch (Exception e) {
            log.error("Ошибка сохранения файла ЖК {}", complexId, e);
            throw new GeneralFormatException(TypeError.SAVE_FILE, file.getOriginalFilename());
        }
    }

    /**
     * Удаляет всю директорию ЖК вместе с файлами.
     */
    public void deleteComplexFiles(Long complexId) {
        Path complexDir = Paths.get(storageRoot, "complexes", String.valueOf(complexId));
        if (!Files.exists(complexDir)) {
            log.warn("Папка для ЖК {} не найдена, пропускаем", complexId);
            return;
        }
        try {
            Files.walk(complexDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            log.error("Не удалось удалить: {}", path, e);
                        }
                    });
            log.info("Папка ЖК {} удалена", complexId);
        } catch (IOException e) {
            log.error("Ошибка удаления папки ЖК {}: {}", complexId, e.getMessage());
        }
    }

    /**
     * Возвращает ресурс изображения ЖК или дефолтную картинку.
     */
    public Resource getRenderResource(ResidentialComplex complex) {
        Path filePath = complex.getRenderPath() == null
                ? Paths.get(storageRoot, "complexes/default.jpg")
                : Paths.get(storageRoot, complex.getRenderPath());
        if (!Files.exists(filePath)) {
            filePath = Paths.get(storageRoot, "complexes/default.jpg");
        }
        return new FileSystemResource(filePath.toFile());
    }
}