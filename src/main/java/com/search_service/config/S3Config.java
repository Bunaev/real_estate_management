package com.search_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                // Адрес MinIO (вместо стандартного AWS S3)
                .endpointOverride(URI.create(endpoint))
                // Логин/пароль
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                // Регион обязателен, но для MinIO не важен
                .region(Region.US_EAST_1)
                // ВАЖНО для MinIO: использовать path-style URL
                // (http://localhost:9000/bucket/key вместо http://bucket.localhost:9000/key)
                .forcePathStyle(true)
                .build();
    }
}