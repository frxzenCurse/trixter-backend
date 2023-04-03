package com.practice.trixter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.trixter.dto.UserDto;
import com.practice.trixter.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@WithMockUser
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    void itShouldGetUser() throws Exception {
        // given
        String authToken = "test-token";
        UserDto userDto = UserDto.builder()
                .username("test-user")
                .build();

        // when
        when(userService.getUser(authToken)).thenReturn(userDto);
        ResultActions resultActions = mockMvc.perform(
                get("/api/user/")
                        .with(csrf())
                        .contentType("application/json")
                        .header(HttpHeaders.AUTHORIZATION, authToken)
        );

        // then
        verify(userService, times(1)).getUser(authToken);
        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).isEqualTo(objectMapper.writeValueAsString(userDto));
    }

    @Test
    void itShouldUpdateUser() throws Exception {
        // given
        UserDto request = UserDto.builder()
                .username("test-user")
                .build();

        // when
        when(userService.update(any(UserDto.class))).thenReturn(request);
        ResultActions resultActions = mockMvc.perform(
                post("/api/user/update")
                        .with(csrf())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request))
        );

        // then
        verify(userService, times(1)).update(any(UserDto.class));
        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).isEqualTo(objectMapper.writeValueAsString(request));
    }

    @Test
    void itShouldUpdateAvatar() throws Exception {
        // given
        String authToken = "test-token";
        UserDto userDto = UserDto.builder()
                .username("test-user")
                .build();
        MockMultipartFile request = new MockMultipartFile(
                "file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "Test bytes".getBytes()
        );


        // when
        when(userService.update(any(), anyString())).thenReturn(userDto);
        ResultActions resultActions = mockMvc.perform(
                multipart("/api/user/update/avatar")
                        .file(request)
                        .header(HttpHeaders.AUTHORIZATION, authToken)
                        .with(csrf())
        );

        // then
        verify(userService, times(1)).update(any(), anyString());
        String result = resultActions.andExpect(status().isOk())
                .andReturn().getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        assertThat(result).isEqualTo(objectMapper.writeValueAsString(userDto));
    }
}