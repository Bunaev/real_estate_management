package com.search_service.service;

import com.search_service.dto.in.ApartmentImportDTO;
import com.search_service.dto.out.response.ApartmentImportResponse;
import com.search_service.dto.out.response.GeneralImportResponse;
import com.search_service.dto.out.response.ImportStatus;
import com.search_service.entity.Apartment;
import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import com.search_service.repository.ApartmentRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApartmentRenderServiceTest {

    private static final Long APARTMENT_ID = 1L;
    private static final Integer APARTMENT_NUMBER = 101;

    @Mock
    private ApartmentRepo apartmentRepo;
    @Mock
    private S3StorageService storageService;
    @InjectMocks
    private ApartmentRenderService service;

    @Test
    void importRenders_shouldUploadBothRendersAndMarkOk() throws IOException {
        Apartment apartment = apartment();
        when(apartmentRepo.findById(APARTMENT_ID)).thenReturn(Optional.of(apartment));
        when(apartmentRepo.save(apartment)).thenReturn(apartment);
        when(storageService.uploadApartmentPublicFile(eq(APARTMENT_ID), any(byte[].class), eq("101.jpg"))).thenReturn("plan-key");
        when(storageService.uploadApartmentPublicFile(eq(APARTMENT_ID), any(byte[].class), eq("101-entrance.jpg"))).thenReturn("entrance-key");

        GeneralImportResponse response = service.importRenders(
                archive(Map.of("101.jpg", new byte[]{1}, "101-entrance.jpg", new byte[]{2})),
                List.of(dto()));

        assertThat(response.getTotal()).isEqualTo(1);
        assertThat(response.getSuccess()).isEqualTo(1);
        assertThat(response.getError()).isZero();
        assertThat(response.getResult().get(0).getStatus()).isEqualTo(ImportStatus.OK);
        assertThat(apartment.getPlanKey()).isEqualTo("plan-key");
        assertThat(apartment.getEntrancePlanKey()).isEqualTo("entrance-key");
        verify(apartmentRepo).save(apartment);
    }

    @Test
    void importRenders_shouldMarkPartial_whenOnlyOneRenderFound() throws IOException {
        Apartment apartment = apartment();
        when(apartmentRepo.findById(APARTMENT_ID)).thenReturn(Optional.of(apartment));
        when(apartmentRepo.save(apartment)).thenReturn(apartment);
        when(storageService.uploadApartmentPublicFile(anyLong(), any(byte[].class), anyString())).thenReturn("plan-key");

        GeneralImportResponse response = service.importRenders(
                archive(Map.of("101.jpg", new byte[]{1})),
                List.of(dto()));

        assertThat(response.getResult().get(0).getStatus()).isEqualTo(ImportStatus.PARTIAL);
        assertThat(response.getSuccess()).isZero();
        assertThat(response.getError()).isEqualTo(1);
        assertThat(apartment.getPlanKey()).isEqualTo("plan-key");
        assertThat(apartment.getEntrancePlanKey()).isNull();
    }

    @Test
    void importRenders_shouldMarkFileNotFound_whenArchiveHasNoRendersForApartment() throws IOException {
        Apartment apartment = apartment();
        when(apartmentRepo.findById(APARTMENT_ID)).thenReturn(Optional.of(apartment));

        GeneralImportResponse response = service.importRenders(
                archive(Map.of("999.jpg", new byte[]{1})),
                List.of(dto()));

        ApartmentImportResponse item = response.getResult().get(0);
        assertThat(item.getStatus()).isEqualTo(ImportStatus.FILE_NOT_FOUND);
        assertThat(item.getMessage()).contains("101.jpg");
        assertThat(item.getMessage()).contains("101-entrance.jpg");
        verify(apartmentRepo, never()).save(any(Apartment.class));
        verifyNoInteractions(storageService);
    }

    @Test
    void importRenders_shouldMarkError_whenApartmentDoesNotExist() throws IOException {
        when(apartmentRepo.findById(APARTMENT_ID)).thenReturn(Optional.empty());

        GeneralImportResponse response = service.importRenders(
                archive(Map.of("101.jpg", new byte[]{1})),
                List.of(dto()));

        assertThat(response.getResult().get(0).getStatus()).isEqualTo(ImportStatus.ERROR);
        verifyNoInteractions(storageService);
    }

    @Test
    void importRenders_shouldMarkError_whenUploadFails() throws IOException {
        Apartment apartment = apartment();
        when(apartmentRepo.findById(APARTMENT_ID)).thenReturn(Optional.of(apartment));
        when(storageService.uploadApartmentPublicFile(anyLong(), any(byte[].class), anyString()))
                .thenThrow(new GeneralFormatException(TypeError.SAVE_FILE, "boom"));

        GeneralImportResponse response = service.importRenders(
                archive(Map.of("101.jpg", new byte[]{1})),
                List.of(dto()));

        assertThat(response.getResult().get(0).getStatus()).isEqualTo(ImportStatus.ERROR);
        verify(apartmentRepo, never()).save(any(Apartment.class));
    }

    @Test
    void importRenders_shouldReturnEmptyResponse_whenApartmentsListIsEmpty() {
        GeneralImportResponse response = service.importRenders(null, List.of());

        assertThat(response.getTotal()).isZero();
        assertThat(response.getResult()).isEmpty();
    }

    @Test
    void importRenders_shouldResolveFileNamesByDtoNumber() throws IOException {
        Apartment apartment = Apartment.builder().id(APARTMENT_ID).number(999).build();
        when(apartmentRepo.findById(APARTMENT_ID)).thenReturn(Optional.of(apartment));
        when(apartmentRepo.save(apartment)).thenReturn(apartment);
        when(storageService.uploadApartmentPublicFile(anyLong(), any(byte[].class), anyString())).thenReturn("plan-key");

        GeneralImportResponse response = service.importRenders(
                archive(Map.of("101.jpg", new byte[]{1})),
                List.of(dto()));

        assertThat(response.getResult().get(0).getStatus()).isEqualTo(ImportStatus.PARTIAL);
        assertThat(apartment.getPlanKey()).isEqualTo("plan-key");
        verify(storageService).uploadApartmentPublicFile(eq(APARTMENT_ID), any(byte[].class), eq("101.jpg"));
    }

    @Test
    void importRenders_shouldMarkError_whenDtoHasNoNumber() throws IOException {
        ApartmentImportDTO dto = ApartmentImportDTO.builder().id(APARTMENT_ID).build();

        GeneralImportResponse response = service.importRenders(
                archive(Map.of("101.jpg", new byte[]{1})),
                List.of(dto));

        assertThat(response.getResult().get(0).getStatus()).isEqualTo(ImportStatus.ERROR);
        verifyNoInteractions(apartmentRepo, storageService);
    }

    @Test
    void updateRenders_shouldUploadFilesAndDeleteOldKeysAfterSave() {
        Apartment apartment = Apartment.builder()
                .id(APARTMENT_ID)
                .number(APARTMENT_NUMBER)
                .planKey("old-plan")
                .entrancePlanKey("old-entrance")
                .build();
        MockMultipartFile planFile = new MockMultipartFile("planFile", "plan.jpg", "image/jpeg", new byte[]{1});
        MockMultipartFile entranceFile = new MockMultipartFile("entrancePlanFile", "entrance.jpg", "image/jpeg", new byte[]{2});

        when(storageService.uploadApartmentPublicFile(eq(APARTMENT_ID), any(byte[].class), eq("plan.jpg")))
                .thenReturn("new-plan");
        when(storageService.uploadApartmentPublicFile(eq(APARTMENT_ID), any(byte[].class), eq("entrance.jpg")))
                .thenReturn("new-entrance");
        when(storageService.getPublicBucket()).thenReturn("bucket");

        service.updateRenders(apartment, null, null, planFile, entranceFile);
        service.deleteReplacedRenders("old-plan", "old-entrance",
                apartment.getPlanKey(), apartment.getEntrancePlanKey());

        assertThat(apartment.getPlanKey()).isEqualTo("new-plan");
        assertThat(apartment.getEntrancePlanKey()).isEqualTo("new-entrance");
        verify(storageService).deleteObject("bucket", "old-plan");
        verify(storageService).deleteObject("bucket", "old-entrance");
    }

    @Test
    void updateRenders_shouldUseDtoKeys_whenFilesAbsent() {
        Apartment apartment = Apartment.builder()
                .id(APARTMENT_ID)
                .number(APARTMENT_NUMBER)
                .planKey("old-plan")
                .entrancePlanKey("old-entrance")
                .build();

        service.updateRenders(apartment, "new-plan", null, null, null);
        when(storageService.getPublicBucket()).thenReturn("bucket");
        service.deleteReplacedRenders("old-plan", "old-entrance",
                apartment.getPlanKey(), apartment.getEntrancePlanKey());

        assertThat(apartment.getPlanKey()).isEqualTo("new-plan");
        assertThat(apartment.getEntrancePlanKey()).isNull();
        verify(storageService).deleteObject("bucket", "old-plan");
        verify(storageService).deleteObject("bucket", "old-entrance");
    }

    @Test
    void updateRenders_shouldSkipDelete_whenKeysAreSame() {
        Apartment apartment = Apartment.builder()
                .id(APARTMENT_ID)
                .number(APARTMENT_NUMBER)
                .planKey("same-plan")
                .entrancePlanKey("same-entrance")
                .build();

        service.updateRenders(apartment, "same-plan", "same-entrance", null, null);
        service.deleteReplacedRenders("same-plan", "same-entrance",
                apartment.getPlanKey(), apartment.getEntrancePlanKey());

        assertThat(apartment.getPlanKey()).isEqualTo("same-plan");
        assertThat(apartment.getEntrancePlanKey()).isEqualTo("same-entrance");
        verify(storageService, never()).deleteObject(anyString(), anyString());
    }

    private Apartment apartment() {
        return Apartment.builder().id(APARTMENT_ID).number(APARTMENT_NUMBER).build();
    }

    private ApartmentImportDTO dto() {
        return ApartmentImportDTO.builder().id(APARTMENT_ID).number(APARTMENT_NUMBER).build();
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
