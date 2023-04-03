package com.practice.trixter.controller;

import com.practice.trixter.model.FilesInfo;
import com.practice.trixter.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/message")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/upload")
    public ResponseEntity<FilesInfo> uploadFile(@RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(messageService.uploadFile(file));
    }

    @PostMapping("/deleteFile")
    public void deleteFile(@RequestBody FilesInfo file) {
        messageService.deleteFile(file);
    }
}
