package com.practice.trixter.controller;

import com.practice.trixter.dto.LoginFormDto;
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

//    @PostMapping("/login")
//    public ResponseEntity<UserDto> loginUser(@RequestBody LoginFormDto loginFormDto) {
//        return ResponseEntity.ok(userService.login(loginFormDto));
//    }
}
