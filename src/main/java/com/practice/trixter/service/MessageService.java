package com.practice.trixter.service;

import com.practice.trixter.enums.Buckets;
import com.practice.trixter.model.FilesInfo;
import com.practice.trixter.model.Message;
import com.practice.trixter.repo.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepo messageRepo;
    private final BlobStorageService storageService;

    public Message save(Message message) {
        return messageRepo.insert(message);
    }

    public FilesInfo uploadFile(MultipartFile file) throws IOException {
        return storageService.uploadFile(Buckets.MESSAGE_FILES.getBucketName(), file);
    }

    public void deleteFile(FilesInfo file) {
        storageService.deleteFile(Buckets.MESSAGE_FILES.getBucketName(), file);
    }
}
