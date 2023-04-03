package com.practice.trixter.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.practice.trixter.enums.Buckets;
import com.practice.trixter.model.FilesInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BlobStorageServiceTest {

    @Mock private AmazonS3 s3client;
    private BlobStorageService underTest;

    @BeforeEach
    void setUp() {
        underTest = new BlobStorageService(s3client);
    }

    @Test
    void canUploadFile() throws IOException {
        // given
        String bucketName = Buckets.MESSAGE_FILES.getBucketName();
        MultipartFile file = new MockMultipartFile(
                "test",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test-bytes".getBytes()
        );
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/jpeg");

        // when
        FilesInfo expected = underTest.uploadFile(bucketName, file);

        // then
        verify(s3client).putObject(anyString(), anyString(), any(), any());
        assertThat(expected.getSize()).isEqualTo(file.getSize());
    }

    @Test
    void canDeleteFile() {
        // given
        String bucketName = Buckets.MESSAGE_FILES.getBucketName();
        FilesInfo filesInfo = FilesInfo.builder()
                .name("test-file")
                .build();

        // when
        underTest.deleteFile(bucketName, filesInfo);

        // then
        verify(s3client).deleteObject(bucketName, filesInfo.getName());
    }
}