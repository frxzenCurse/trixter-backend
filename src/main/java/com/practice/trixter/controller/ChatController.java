package com.practice.trixter.controller;

import com.practice.trixter.dto.ChatDto;
import com.practice.trixter.dto.MessageDto;
import com.practice.trixter.model.Chat;
import com.practice.trixter.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/message")
    public void receiveMessage(@Payload MessageDto messageDto) {
        chatService.receiveMessage(messageDto);
    }

    @PostMapping("/api/chat/get")
    public ResponseEntity<List<ChatDto>> getChats(@RequestBody List<String> chatsId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        return ResponseEntity.ok(chatService.getChats(chatsId, authToken));
    }

    @GetMapping("/api/chat/{chatId}")
    public ResponseEntity<ChatDto> getChat(@PathVariable String chatId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authToken) {
        return ResponseEntity.ok(chatService.getChat(chatId, authToken));
    }

    @PostMapping("api/chat/update")
    public ResponseEntity<ChatDto> updateChat(@RequestBody Chat chat) {
        return ResponseEntity.ok(chatService.update(chat));
    }

    @PostMapping("/api/chat/")
    public ResponseEntity<ChatDto> addChat(@RequestBody ChatDto chatDto) {
        return ResponseEntity.ok(chatService.save(chatDto));
    }
}
