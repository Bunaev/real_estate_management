package com.search_service.util;

import com.search_service.exception.GeneralFormatException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ZipUtilsTest {

    @Test
    void readImages_shouldReturnOnlyRootLevelImages() throws IOException {
        MockMultipartFile archive = archive(Map.of(
                "101.jpg", new byte[]{1},
                "101-entrance.jpg", new byte[]{2},
                "renders/102.jpg", new byte[]{3},
                "readme.txt", new byte[]{4}));

        Map<String, byte[]> images = ZipUtils.readImages(archive);

        assertThat(images).containsOnlyKeys("101.jpg", "101-entrance.jpg");
    }

    @Test
    void readImages_shouldNormalizeNamesToLowerCase() throws IOException {
        MockMultipartFile archive = archive(Map.of("101.JPG", new byte[]{1}));

        Map<String, byte[]> images = ZipUtils.readImages(archive);

        assertThat(images).containsKey("101.jpg");
    }

    @Test
    void readImages_shouldThrowWhenArchiveIsEmpty() {
        MockMultipartFile archive = new MockMultipartFile("file", "renders.zip", "application/zip", new byte[0]);

        assertThatThrownBy(() -> ZipUtils.readImages(archive))
                .isInstanceOf(GeneralFormatException.class);
    }

    @Test
    void readImages_shouldThrowWhenArchiveContainsTooManyEntries() throws IOException {
        Map<String, byte[]> entries = new LinkedHashMap<>();
        for (int i = 0; i <= 1000; i++) {
            entries.put(i + ".jpg", new byte[]{1});
        }
        MockMultipartFile archive = archive(entries);

        assertThatThrownBy(() -> ZipUtils.readImages(archive))
                .isInstanceOf(GeneralFormatException.class);
    }

    private MockMultipartFile archive(Map<String, byte[]> entries) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ZipOutputStream zip = new ZipOutputStream(out)) {
            for (Map.Entry<String, byte[]> entry : entries.entrySet()) {
                zip.putNextEntry(new ZipEntry(entry.getKey()));
                zip.write(entry.getValue());
                zip.closeEntry();
            }
        }
        return new MockMultipartFile("file", "renders.zip", "application/zip", out.toByteArray());
    }
}
