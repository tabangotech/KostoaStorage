package com.example.svcstorage.service;

import com.example.svcstorage.exception.StorageException;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class StorageService {

    private static final Logger log = LoggerFactory.getLogger(StorageService.class);

    private final MinioClient minioClient;
    private final String bucketName;

    public StorageService(MinioClient minioClient, @Value("${minio.bucketName}") String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    public String uploadFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new StorageException("Failed to store empty file.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.lastIndexOf(".") > -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID().toString() + extension;

        try (InputStream inputStream = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(uniqueFileName)
                            .stream(inputStream, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            log.info("[svc-storage] Successfully uploaded file: {}", uniqueFileName);
            return uniqueFileName;
        } catch (Exception e) {
            log.error("[svc-storage] Error uploading file: {}", e.getMessage(), e);
            throw new StorageException("Error uploading file to MinIO", e);
        }
    }

    public String getFileUrl(String filename) {
        try {
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(filename)
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );
            log.info("[svc-storage] Generated pre-signed URL for file: {}", filename);
            return url;
        } catch (Exception e) {
            log.error("[svc-storage] Error getting file URL for {}: {}", filename, e.getMessage(), e);
            throw new StorageException("Error generating pre-signed URL", e);
        }
    }
}
