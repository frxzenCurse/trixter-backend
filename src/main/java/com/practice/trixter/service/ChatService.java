package com.practice.trixter.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.practice.trixter.dto.ChatDto;
import com.practice.trixter.dto.UserDto;
import com.practice.trixter.exceptions.BadChatRequestException;
import com.practice.trixter.model.Chat;
import com.practice.trixter.model.Message;
import com.practice.trixter.model.User;
import com.practice.trixter.repo.ChatRepo;
import com.practice.trixter.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepo chatRepo;
    private final UserService userService;

    public Chat update(Chat chat, Message message) {
        chat.addMessage(message);

        return chatRepo.save(chat);
    }

    public ChatDto update(Chat chat) {
        chat = chatRepo.save(chat);

        return convertToDto(chat);
    }

    public ChatDto save(Chat chat) {
        chat = chatRepo.insert(chat);

        String chatId = chat.getId();
        chat.getMembersId().forEach(memberId -> {
            User user = userService.find(memberId);
            user.addChatId(chatId);
            userService.update(user);
        });

        return convertToDto(chat);
    }

    public ChatDto getChat(String chatId, String authHeader) {
        User user = userService.getUserByToken(authHeader);

        if (!user.getChatsIds().contains(chatId)) {
            throw new BadChatRequestException("Пользователь " + user.getUsername() + " не находится в данном чате");
        }

        Chat chat = findChat(chatId);

        return convertToDto(chat);
    }

    public List<ChatDto> getChats(List<String> chatsId, String authHeader) {
        User user = userService.getUserByToken(authHeader);
        List<Chat> chats = chatRepo.findAllById(chatsId);
        List<ChatDto> chatsDto = new ArrayList<>();

        chats.forEach(chat -> {
            if (!user.getChatsIds().contains(chat.getId())) {
                throw new BadChatRequestException("Пользователь " + user.getUsername() + " не находится в данном чате");
            }

            chatsDto.add(convertToDto(chat));
        });

        return chatsDto;
    }

    public ChatDto convertToDto(Chat chat) {
        List<UserDto> users = new ArrayList<>();

        chat.getMembersId().forEach(id -> {
            User user = userService.find(id);
            UserDto userDto = userService.convertToDto(user);
            userDto.setChatsId(null);
            users.add(userDto);
        });

        return ChatDto.builder()
                .id(chat.getId())
                .name(chat.getName())
                .members(users)
                .messages(chat.getMessages())
                .build();
    }

    public Chat findChat(String id) {
        return chatRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("chat not found"));
    }
}
