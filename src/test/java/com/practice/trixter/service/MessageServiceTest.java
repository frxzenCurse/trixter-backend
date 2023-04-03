package com.practice.trixter.service;

import com.practice.trixter.enums.Buckets;
import com.practice.trixter.model.FilesInfo;
import com.practice.trixter.model.Message;
import com.practice.trixter.repo.MessageRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock private MessageRepo messageRepo;
    @Mock private BlobStorageService storageService;

    private MessageService underTest;

    @BeforeEach
    void setUp() {
        underTest = new MessageService(messageRepo, storageService);
    }

    @Test
    void canSaveMessage() {
        // given
        Message message = Message.builder()
                .body("test-body")
                .build();

        // when
        when(messageRepo.insert(message)).thenReturn(message);
        Message expected = underTest.save(message);

        // then
        verify(messageRepo).insert(message);
        assertThat(expected.getBody()).isEqualTo(message.getBody());
    }

    @Test
    void canUploadFile() throws IOException {
        // given
        String fileName = "img.jpeg";
        MultipartFile file = new MockMultipartFile(
            "file",
            fileName,
            MediaType.IMAGE_JPEG_VALUE,
            "Hello world".getBytes()
        );
        FilesInfo filesInfo = FilesInfo.builder()
                .name(fileName)
                .build();

        // when
        when(storageService.uploadFile(Buckets.MESSAGE_FILES.getBucketName(), file)).thenReturn(filesInfo);
        FilesInfo expected = underTest.uploadFile(file);

        // then
        verify(storageService).uploadFile(Buckets.MESSAGE_FILES.getBucketName(), file);
        assertThat(expected.getName()).isEqualTo(fileName);
    }

    @Test
    void canDeleteFile() {
        // given
        String fileName = "img.jpeg";
        FilesInfo filesInfo = FilesInfo.builder()
                .name(fileName)
                .build();

        // when
        underTest.deleteFile(filesInfo);

        // then
        ArgumentCaptor<FilesInfo> argumentCaptor = ArgumentCaptor.forClass(FilesInfo.class);
        verify(storageService).deleteFile(anyString(), argumentCaptor.capture());

        FilesInfo expected = argumentCaptor.getValue();
        assertThat(expected.getName()).isEqualTo(fileName);
    }
}