package com.practice.trixter.controller;

import com.practice.trixter.dto.RegisterFormDto;
import com.practice.trixter.dto.UserDto;
import com.practice.trixter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestBody RegisterFormDto form) {
        return ResponseEntity.ok(userService.register(form));
    }
}
