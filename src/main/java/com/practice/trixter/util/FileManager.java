package com.practice.trixter.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Component
@Slf4j
public class FileManager {
    private static final String DIRECTORY_PATH = "C:\\projects\\trixter-front\\src\\assets\\user\\";

    public static String upload(MultipartFile file, String filename) throws IOException {
        String extension = getExtension(file.getOriginalFilename());
        Path path = Paths.get(DIRECTORY_PATH, filename+extension);
        Path newFile = Files.createFile(path);
        InputStream data = file.getInputStream();

        try (FileOutputStream stream = new FileOutputStream(newFile.toString())) {
            byte[] bytes = data.readAllBytes();
            stream.write(bytes);
        } catch (Exception e) {
            log.info("UPLOAD EXCEPTION - {}", e.getMessage());
        }
        log.info("UPLOAD PATH - {}", newFile);
        data.close();
        return newFile.toString();
    }

    public static String generateKey(String filename) {
        String key = filename + LocalDateTime.now();
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String getExtension(String originalName) {
        return originalName.substring(originalName.lastIndexOf("."));
    }

    public static void delete(String filename) throws IOException {
        Path path = Paths.get(DIRECTORY_PATH, filename);
        Files.delete(path);
    }
}