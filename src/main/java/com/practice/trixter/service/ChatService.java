package com.practice.trixter.service;

import com.practice.trixter.dto.ChatDto;
import com.practice.trixter.dto.MessageDto;
import com.practice.trixter.dto.UserDto;
import com.practice.trixter.enums.ChatType;
import com.practice.trixter.exceptions.BadChatRequestException;
import com.practice.trixter.exceptions.ChatNotFoundException;
import com.practice.trixter.model.*;
import com.practice.trixter.repo.ChatRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatRepo chatRepo;
    private final UserService userService;
    private final MessageService messageService;
    private final SimpMessagingTemplate template;

    public void receiveMessage(MessageDto messageDto) {
        User user = userService.find(messageDto.getSender().getId());
        Message message = Message.builder()
                .body(messageDto.getBody())
                .chatId(messageDto.getChatId())
                .sender(user)
                .build();

        message = messageService.save(message);
        Chat chat = findChat(message.getChatId());
        update(chat, message);
        template.convertAndSend("/chatroom/" + chat.getId(), message.convertToDto());
    }


    public Chat update(Chat chat, Message message) {
        chat.addMessage(message);

        return chatRepo.save(chat);
    }

    public ChatDto update(Chat chat) {
        chat = chatRepo.save(chat);

        return convertToDto(chat);
    }

    public ChatDto save(ChatDto chatDto) {
        List<User> users = chatDto.getMembers().stream()
                .map(UserDto::getId)
                .map(userService::find)
                .collect(Collectors.toList());

        Chat chat = createChat(chatDto, users);
        chat = chatRepo.insert(chat);

        Chat finalChat = chat;
        users.forEach(user -> {
            user.addChat(finalChat);
            userService.update(user);
        });

        return convertToDto(chat);
    }

    public ChatDto getChat(String chatId, String authHeader) {
        User user = userService.getUserByToken(authHeader);
        Chat chat = findChat(chatId);

        if (!chat.getMembersIds().contains(user.getId())) {
            throw new BadChatRequestException("Пользователь " + user.getUsername() + " не находится в данном чате");
        }

        return convertToDto(chat);
    }

    public List<ChatDto> getChats(List<String> chatsId, String authHeader) {
        User user = userService.getUserByToken(authHeader);
        List<Chat> chats = chatRepo.findAllById(chatsId);
        List<ChatDto> chatsDto = new ArrayList<>();

        chats.forEach(chat -> {
            if (!chat.getMembersIds().contains(user.getId())) {
                throw new BadChatRequestException("Пользователь " + user.getUsername() + " не находится в данном чате");
            }

            chatsDto.add(convertToDto(chat));
        });

        return chatsDto;
    }

    public ChatDto convertToDto(Chat chat) {
        List<UserDto> users = new ArrayList<>();

        chat.getMembers().forEach(user -> {
            UserDto userDto = user.convertToDto();
            userDto.setChatsIds(null);
            users.add(userDto);
        });

        List<MessageDto> messages = chat.getMessages().stream()
                .map(Message::convertToDto)
                .collect(Collectors.toList());

        ChatDto chatDto = ChatDto.builder()
                .id(chat.getId())
                .name(chat.getName())
                .type(chat.getType().getType())
                .members(users)
                .messages(messages)
                .build();

        if (chat instanceof Group) {
            Group group = (Group) chat;
            List<UserDto> admins = group.getAdministration().stream()
                            .map(User::convertToDto)
                            .collect(Collectors.toList());

            chatDto.setOwner(group.getOwner().convertToDto());
            chatDto.setAdministration(admins);
        }

        return chatDto;
    }

    public Chat findChat(String id) {
        return chatRepo.findById(id)
                .orElseThrow(() -> new ChatNotFoundException("Чат с id " + id + " не был найден"));
    }

    private Chat createChat(ChatDto chatDto, List<User> users) {
        String type = chatDto.getType();
        Chat chat = null;

        if (type.equals(ChatType.PRIVATE_CHAT.getType())) {
            chat = Chat.builder()
                    .name(chatDto.getName())
                    .type(ChatType.PRIVATE_CHAT)
                    .members(users)
                    .messages(new ArrayList<>())
                    .build();
        }

        if (type.equals(ChatType.GROUP.getType())) {
            User owner = userService.find(chatDto.getOwner().getId());

            chat = Group.builder()
                    .name(chatDto.getName())
                    .members(users)
                    .messages(new ArrayList<>())
                    .owner(owner)
                    .type(ChatType.GROUP)
                    .administration(List.of(owner))
                    .build();
        }

        if (type.equals(ChatType.CHANNEL.getType())) {
            User owner = userService.find(chatDto.getOwner().getId());

            chat = Channel.builder()
                    .name(chatDto.getName())
                    .members(users)
                    .messages(new ArrayList<>())
                    .owner(owner)
                    .type(ChatType.CHANNEL)
                    .administration(List.of(owner))
                    .build();
        }

        if (chat == null) {
            throw new RuntimeException("INVALID CHAT TYPE");
        }

        return chat;
    }
}
