package com.search_service.service;

import com.search_service.exception.GeneralFormatException;
import com.search_service.exception.TypeError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3StorageService {

    private static final String COMPLEX_FOLDER = "complexes";
    private static final String APARTMENT_FOLDER = "apartments";

    private final S3Client s3Client;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.public-bucket}")
    @Getter
    private String publicBucket;

    public String uploadComplexPublicFile(Long complexId, byte[] content, String originalName) {
        return uploadPublicFile(COMPLEX_FOLDER + "/" + complexId + "/", content, originalName);
    }

    public String uploadApartmentPublicFile(Long apartmentId, byte[] content, String originalName) {
        return uploadPublicFile(APARTMENT_FOLDER + "/" + apartmentId + "/", content, originalName);
    }

    private String uploadPublicFile(String path, byte[] content, String originalName) {
        String key = path + UUID.randomUUID() + "_" + sanitize(originalName);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(publicBucket)
                .key(key)
                .contentType(resolveContentType(originalName))
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(content));
        return key;
    }

    public String uploadPublicFile(String path, byte[] content, String contentType, String originalName) {
        String key = path + sanitize(originalName);
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(publicBucket)
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(content));
        return key;
    }

    public String generatePublicDocumentPath(Long complexId) {
        return generateFolder(COMPLEX_FOLDER + "/" + complexId + "/documents/");
    }

    public String generatePublicPresentationPath(Long complexId) {
        return generateFolder(COMPLEX_FOLDER + "/" + complexId + "/presentation/");
    }

    private String generateFolder(String key) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(publicBucket)
                .key(key)
                .contentLength(0L)
                .build();
        s3Client.putObject(request, RequestBody.empty());
        return key;
    }

    public String generatePublicDownloadUrl(String key) {
        return endpoint.replaceAll("/+$", "") + "/" + publicBucket + "/" + key;
    }

    public boolean doesPublicObjectExist(String key) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(publicBucket)
                    .key(key)
                    .build());
            return true;
        } catch (NoSuchKeyException exception) {
            return false;
        }
    }

    public void deleteObject(String bucket, String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }

    public void deletePublicFile(String key) {
        deleteObject(publicBucket, key);
    }

    private String sanitize(String filename) {
        if (filename == null) return "file";
        return filename.replaceAll("[\\\\/:*?\"<>|]", "_").replaceAll("\\s+", "_");
    }

    private String resolveContentType(String name) {
        if (name == null) {
            throw new GeneralFormatException(TypeError.SAVE_FILE, "Пустое имя файла.");
        }
        String lower = name.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".png"))  return "image/png";
        if (lower.endsWith(".webp")) return "image/webp";
        if (lower.endsWith(".gif"))  return "image/gif";
        return "image/jpeg";
    }
}