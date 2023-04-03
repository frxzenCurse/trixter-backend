package com.practice.trixter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.trixter.model.FilesInfo;
import com.practice.trixter.service.MessageService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MessageController.class)
@WithMockUser
class MessageControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MessageService messageService;

    @Test
    void itShouldUploadFile() throws Exception  {
        // given
        MockMultipartFile request = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test bytes".getBytes()
        );
        FilesInfo filesInfo = new FilesInfo("test-url", request.getOriginalFilename(), request.getSize());

        // when
        when(messageService.uploadFile(any())).thenReturn(filesInfo);
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/message/upload")
                        .file(request)
                        .with(csrf())
        );

        // then
        verify(messageService, times(1)).uploadFile(any());
        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).isEqualTo(objectMapper.writeValueAsString(filesInfo));
    }

    @Test
    void itShouldDeleteFile() throws Exception  {
        // given
        FilesInfo request = new FilesInfo("test-url", "test", 123L);

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/message/deleteFile")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        ArgumentCaptor<FilesInfo> argumentCaptor = ArgumentCaptor.forClass(FilesInfo.class);
        verify(messageService, times(1)).deleteFile(argumentCaptor.capture());
        resultActions.andExpect(status().isOk());
        FilesInfo expected = argumentCaptor.getValue();
        assertThat(expected.getName()).isEqualTo(request.getName());
        assertThat(expected.getUrl()).isEqualTo(request.getUrl());
        assertThat(expected.getSize()).isEqualTo(request.getSize());
    }
}