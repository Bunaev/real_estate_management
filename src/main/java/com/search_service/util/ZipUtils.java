package com.search_service.util;

import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ZipUtils {

    private static final int BUFFER_SIZE = 8192;
    private static final int MAX_ENTRIES = 1000;
    private static final long MAX_ENTRY_SIZE = 15L * 1024 * 1024;
    private static final List<String> IMAGE_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".webp");

    private ZipUtils() {
        throw new UnsupportedOperationException("Нельзя создать экземпляр утилитного класса: " + ZipUtils.class.getSimpleName());
    }

    public static Map<String, byte[]> readImages(MultipartFile archive) {
        if (archive == null || archive.isEmpty()) {
            throw new GeneralFormatException(TypeError.FILE_IS_EMPTY, "Загрузите zip-архив с планировками.");
        }
        Map<String, byte[]> images = new LinkedHashMap<>();
        try (ZipInputStream zip = new ZipInputStream(new BufferedInputStream(archive.getInputStream()))) {
            ZipEntry entry;
            int entries = 0;
            while ((entry = zip.getNextEntry()) != null) {
                if (++entries > MAX_ENTRIES) {
                    throw new GeneralFormatException(TypeError.ZIP_ERROR, "Слишком много файлов в архиве.");
                }
                String name = resolveImageName(entry.getName());
                if (name != null) {
                    images.putIfAbsent(name, readEntry(zip, entry.getName()));
                }
                zip.closeEntry();
            }
            return images;
        } catch (IOException exception) {
            throw new GeneralFormatException(TypeError.ZIP_ERROR, "Не удалось прочитать архив.");
        }
    }

    private static String resolveImageName(String entryName) {
        if (entryName == null) {
            return null;
        }
        String normalized = entryName.replace('\\', '/');
        if (normalized.contains("/")) {
            return null;
        }
        String name = normalized.toLowerCase(Locale.ROOT);
        return isImage(name) ? name : null;
    }

    private static boolean isImage(String name) {
        return IMAGE_EXTENSIONS.stream().anyMatch(name::endsWith);
    }

    private static byte[] readEntry(ZipInputStream zip, String entryName) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(BUFFER_SIZE);
        byte[] chunk = new byte[BUFFER_SIZE];
        int read;
        while ((read = zip.read(chunk)) != -1) {
            buffer.write(chunk, 0, read);
            if (buffer.size() > MAX_ENTRY_SIZE) {
                throw new GeneralFormatException(TypeError.ZIP_ERROR, "Файл слишком большой: " + entryName);
            }
        }
        return buffer.toByteArray();
    }
}
