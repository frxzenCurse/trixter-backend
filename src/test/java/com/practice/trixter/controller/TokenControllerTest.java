package com.practice.trixter.controller;

import com.practice.trixter.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(TokenController.class)
@WithMockUser
class TokenControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TokenService tokenService;

    @Test
    void itShouldRefreshToken() throws Exception {

        // when
        ResultActions resultActions = mockMvc.perform(
                get("/api/token/refresh")
                        .with(csrf())
                );

        // then
        verify(tokenService, times(1)).refreshToken(any(), any());
    }

    @Test
    void itShouldRemoveToken() throws Exception {

        // when
        ResultActions resultActions = mockMvc.perform(
                post("/api/token/remove")
                        .with(csrf())
        );

        // then
        verify(tokenService, times(1)).removeToken(any(), any());
    }
}