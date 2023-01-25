package com.practice.trixter.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.practice.trixter.model.User;
import com.practice.trixter.service.UserService;
import com.practice.trixter.util.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("api/token")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final UserService userService;

    @GetMapping("/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            returnInvalidRequest(response);
            return;
        }

        Optional<String> cookiesValue = Arrays.stream(request.getCookies())
                        .filter(cookie -> cookie.getName().equals("jwt_refresh"))
                        .map(Cookie::getValue)
                        .findAny();
        String token = cookiesValue.orElse(null);

        if (token == null) {
            returnInvalidRequest(response);
            return;
        }

        try {
            DecodedJWT decodedJWT = JWTUtil.verifyToken(token);
            String username = decodedJWT.getSubject();
            User user = userService.findByUsername(username);
            String access_token = JWTUtil.createAccessToken(user, request.getRequestURL().toString());

            Map<String, String> tokens = new HashMap<>();
            tokens.put("access_token", access_token);

            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), tokens);
        } catch (Exception e) {
            response.setHeader("error", e.getMessage());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> error = new HashMap<>();
            error.put("error_message", e.getMessage());

            response.setContentType(APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), error);
        }

    }

    private void returnInvalidRequest(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        Map<String, String> error = new HashMap<>();
        error.put("error_message", "Invalid request");

        response.setContentType(APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), error);
    }
}
