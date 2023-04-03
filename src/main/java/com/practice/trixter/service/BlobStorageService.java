package com.practice.trixter.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.practice.trixter.model.FilesInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class BlobStorageService {

    private final AmazonS3 s3client;
    private final String HOST_URL = "https://storage.yandexcloud.net";

    public FilesInfo uploadFile(String bucketName, MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        String extension = getExtension(fileName);
        String generatedName = generateKey(fileName) + extension;
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(getContentType(extension));

        s3client.putObject(
                bucketName, generatedName, file.getInputStream(), metadata
        );

        return FilesInfo.builder()
                .name(generatedName)
                .url(HOST_URL + "/" + bucketName + "/" + generatedName)
                .size(file.getSize())
                .build();
    }

    public void deleteFile(String bucketName, FilesInfo filesInfo) {
        s3client.deleteObject(bucketName, filesInfo.getName());
    }

    private String generateKey(String filename) {
        String key = filename + LocalDateTime.now();
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    private String getExtension(String originalName) {
        return originalName.substring(originalName.lastIndexOf("."));
    }

    private String getContentType(String extension) {
        switch (extension) {
            case "webp":
                return "image/webp";
            case "png":
                return "image/png";
            default:
                return "image/jpeg";
        }
    }
}
