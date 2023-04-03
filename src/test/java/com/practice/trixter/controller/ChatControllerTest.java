package com.practice.trixter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.trixter.dto.ChatDto;
import com.practice.trixter.model.Chat;
import com.practice.trixter.service.ChatService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ChatController.class)
@WithMockUser
class ChatControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ChatService chatService;

    @Test
    @Disabled
    void canReceiveMessage() {
    }

    @Test
    void itShouldGetChats() throws Exception {
        // given
        String authHeader = "test-header";
        List<String> request = List.of("test-id");
        ChatDto chatDto = ChatDto.builder()
                .name("test-chat")
                .build();

        // when
        when(chatService.getChats(any(), any())).thenReturn(List.of(chatDto));
        ResultActions resultActions = mockMvc.perform(
                post("/api/chat/get")
                        .with(csrf())
                        .contentType("application/json")
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        verify(chatService, times(1)).getChats(any(), any());
        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        List<ChatDto> mapped = objectMapper.readValue(result.getBytes(), List.class);
        assertThat(mapped.size()).isEqualTo(1);
    }

    @Test
    void itShouldGetChat() throws Exception {
        // given
        String authHeader = "test-header";
        String chatId = "test-id";
        ChatDto chatDto = ChatDto.builder()
                .id(chatId)
                .name("test-chat")
                .build();

        // when
        when(chatService.getChat(chatId, authHeader)).thenReturn(chatDto);
        ResultActions resultActions = mockMvc.perform(
                get("/api/chat/" + chatId)
                        .with(csrf())
                        .contentType("application/json")
                        .header(HttpHeaders.AUTHORIZATION, authHeader)
                        .content(objectMapper.writeValueAsString(chatId))
        );

        // then
        verify(chatService, times(1)).getChat(chatId, authHeader);
        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ChatDto mapped = objectMapper.readValue(result.getBytes(), ChatDto.class);
        assertThat(mapped.getName()).isEqualTo(chatDto.getName());
    }

    @Test
    void itShouldUpdateChat() throws Exception  {
        // given
        Chat request = Chat.builder()
                .id("test-id")
                .name("test-chat")
                .members(new ArrayList<>())
                .messages(new ArrayList<>())
                .build();
        ChatDto chatDto = ChatDto.builder()
                .id(request.getId())
                .name(request.getName())
                .build();

        // when
        when(chatService.update(any())).thenReturn(chatDto);
        ResultActions resultActions = mockMvc.perform(
                post("/api/chat/update")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        verify(chatService, times(1)).update(any());
        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ChatDto mapped = objectMapper.readValue(result.getBytes(), ChatDto.class);
        assertThat(mapped.getName()).isEqualTo(chatDto.getName());
    }

    @Test
    void itShouldAddChat() throws Exception  {
        // given
        ChatDto request = ChatDto.builder()
                .name("test-name")
                .build();

        // when
        when(chatService.save(any())).thenReturn(request);
        ResultActions resultActions = mockMvc.perform(
                post("/api/chat/")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        verify(chatService, times(1)).save(any());
        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        ChatDto mapped = objectMapper.readValue(result.getBytes(), ChatDto.class);
        assertThat(mapped.getName()).isEqualTo(request.getName());
    }
}