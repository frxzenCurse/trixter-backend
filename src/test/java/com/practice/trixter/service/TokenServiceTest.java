package com.practice.trixter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.trixter.model.User;
import com.practice.trixter.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock private UserService userService;

    private TokenService underTest;

    @BeforeEach
    void setUp() {
        underTest = new TokenService(userService);
    }

    @Test
    void canRefreshToken() throws IOException {
        // given
        User user = User.builder()
                .username("test-username")
                .build();
        String token = JWTUtil.createAccessToken(user, "test-issuer");
        Cookie jwtRefreshCookie = new Cookie("jwt_refresh", token);
        jwtRefreshCookie.setMaxAge(60 * 60);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(jwtRefreshCookie);
        request.setRequestURI("local.ru/test");
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        when(userService.findByUsername(user.getUsername())).thenReturn(user);
        underTest.refreshToken(request, response);

        // then
        verify(userService).findByUsername(user.getUsername());
        Map<String, String> expected = mapper.readValue(response.getContentAsByteArray(), HashMap.class);
        assertThat(expected.get("access_token")).isNotNull();
    }

    @Test
    void willReturnInvalidRequestWhenEmptyCookies() throws IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        underTest.refreshToken(request, response);

        // then
        Map<String, String> expected = mapper.readValue(response.getContentAsByteArray(), HashMap.class);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(expected.get("error_message")).isEqualTo("Invalid request");
    }

    @Test
    void willReturnInvalidRequestWhenNoRefreshCookie() throws IOException {
        // given
        Cookie cookie = new Cookie("test", "test-token");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        underTest.refreshToken(request, response);

        // then
        Map<String, String> expected = mapper.readValue(response.getContentAsByteArray(), HashMap.class);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(expected.get("error_message")).isEqualTo("Invalid request");
    }

    @Test
    void willReturnInvalidRequestWhenInvalidRefreshToken() throws IOException {
        // given
        Cookie cookie = new Cookie("jwt_refresh", "test-token");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(cookie);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        underTest.refreshToken(request, response);

        // then
        Map<String, String> expected = mapper.readValue(response.getContentAsByteArray(), HashMap.class);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(expected.get("error_message")).isNotNull();
    }

    @Test
    void canRemoveToken() throws IOException {
        // given
        Cookie jwtRefreshCookie = new Cookie("jwt_refresh", "test");
        jwtRefreshCookie.setMaxAge(60 * 60);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(jwtRefreshCookie);
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        underTest.removeToken(request, response);

        // then
        Cookie[] expected = response.getCookies();
        assertThat(expected[0].getMaxAge()).isEqualTo(0);
    }
}