package com.practice.trixter.controller;

import com.practice.trixter.dto.UserDto;
import com.practice.trixter.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
@CrossOrigin
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public ResponseEntity<UserDto> getUser(@RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        return ResponseEntity.ok(userService.getUserWithChats(authToken));
    }

    @PostMapping("/update")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.update(userDto));
    }

    @PostMapping("/update/avatar")
    public ResponseEntity<UserDto> updateAvatar(@RequestParam("file") MultipartFile file, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        return ResponseEntity.ok(userService.update(file, authToken));
    }
}
