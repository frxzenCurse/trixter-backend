package com.practice.trixter.service;

import com.practice.trixter.dto.ChatDto;
import com.practice.trixter.dto.MessageDto;
import com.practice.trixter.dto.UserDto;
import com.practice.trixter.enums.ChatType;
import com.practice.trixter.exceptions.BadChatRequestException;
import com.practice.trixter.exceptions.ChatNotFoundException;
import com.practice.trixter.model.Chat;
import com.practice.trixter.model.Group;
import com.practice.trixter.model.Message;
import com.practice.trixter.model.User;
import com.practice.trixter.repo.ChatRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock private ChatRepo chatRepo;
    @Mock private UserService userService;
    @Mock private MessageService messageService;
    @Mock private SimpMessagingTemplate messagingTemplate;

    private ChatService underTest;

    @BeforeEach
    void setUp() {
        underTest = new ChatService(
                chatRepo,
                userService,
                messageService,
                messagingTemplate
        );
    }

    @Test
    void canReceiveMessage() {
        // given
        UserDto userDto = UserDto.builder()
                .id("dto-id")
                .build();
        User user = User.builder()
                .id("test-id")
                .chats(new ArrayList<>())
                .build();
        MessageDto messageDto = MessageDto.builder()
                .body("test-body")
                .sender(userDto)
                .build();
        Message message = Message.builder()
                .chatId("chat-id")
                .body(messageDto.getBody())
                .sender(user)
                .build();
        Chat chat = Chat.builder()
                .messages(new ArrayList<>())
                .build();

        // when
        when(userService.find(anyString())).thenReturn(user);
        when(messageService.save(any())).thenReturn(message);
        when(chatRepo.findById(message.getChatId())).thenReturn(Optional.of(chat));
        underTest.receiveMessage(messageDto);

        // then
        ArgumentCaptor<MessageDto> argumentCaptor = ArgumentCaptor.forClass(MessageDto.class);
        verify(userService).find(anyString());
        verify(messageService).save(any());
        verify(chatRepo).findById(message.getChatId());
        verify(messagingTemplate).convertAndSend(anyString(), argumentCaptor.capture());

        MessageDto expected = argumentCaptor.getValue();
        assertThat(expected.getChatId()).isEqualTo(message.getChatId());
        assertThat(expected.getBody()).isEqualTo(message.getBody());
    }

    @Test
    void canUpdateChatMessages() {
        // given
        Chat chat = Chat.builder()
                .id("test-id")
                .name("test-chat")
                .messages(new ArrayList<>())
                .build();
        User sender = User.builder()
                .id("user-test-id")
                .username("test-username")
                .build();
        Message message = Message.builder()
                .body("Test-body")
                .sender(sender)
                .build();

        // when
        when(chatRepo.save(chat)).thenReturn(chat);
        Chat expected = underTest.update(chat, message);

        // then
        verify(chatRepo).save(chat);
        assertThat(expected.getName()).isEqualTo(chat.getName());
        assertThat(expected.getMessages().get(0)).isEqualTo(message);
        assertThat(expected.getName()).isEqualTo(chat.getName());
    }

    @Test
    void canUpdateChat() {
        // given
        Chat chat = Chat.builder()
                .id("test-id")
                .name("test-chat")
                .type(ChatType.PRIVATE_CHAT)
                .messages(new ArrayList<>())
                .members(new ArrayList<>())
                .build();

        // when
        when(chatRepo.save(chat)).thenReturn(chat);
        ChatDto expected = underTest.update(chat);

        // then
        verify(chatRepo).save(chat);
        assertThat(expected.getName()).isEqualTo(chat.getName());
        assertThat(expected.getType()).isEqualTo(chat.getType().getType());
    }

    @Test
    void canSaveChat() {
        // given
        UserDto userDto = UserDto.builder()
                .id("test-id")
                .username("test-user-dto")
                .build();
        User user = User.builder()
                .username("test-user")
                .chats(new ArrayList<>())
                .build();
        ChatDto chatDto = ChatDto.builder()
                .name("test-chat")
                .type("private chat")
                .members(List.of(userDto))
                .build();
        Chat chat = Chat.builder()
                .name("test-chat")
                .type(ChatType.PRIVATE_CHAT)
                .members(List.of(user))
                .messages(new ArrayList<>())
                .build();

        // when
        when(chatRepo.insert(any(Chat.class))).thenReturn(chat);
        when(userService.find(userDto.getId())).thenReturn(user);
        ChatDto expected = underTest.save(chatDto);

        // then
        verify(chatRepo).insert(any(Chat.class));
        verify(userService, times(1)).find(userDto.getId());
        assertThat(expected.getName()).isEqualTo(chat.getName());
    }

    @Test
    void canGetChat() {
        // given
        String token = "test-token";
        User user = User.builder()
                .id("test")
                .username("test-user")
                .chats(new ArrayList<>())
                .build();
        Chat chat = Chat.builder()
                .id("test-id")
                .name("test-chat")
                .type(ChatType.PRIVATE_CHAT)
                .messages(new ArrayList<>())
                .members(List.of(user))
                .build();

        // when
        when(userService.getUserByToken(token)).thenReturn(user);
        when(chatRepo.findById(chat.getId())).thenReturn(Optional.of(chat));
        ChatDto expected = underTest.getChat(chat.getId(), token);

        // then
        verify(userService).getUserByToken(token);
        assertThat(expected.getName()).isEqualTo(chat.getName());
    }

    @Test
    void willThrowBadChatRequest() {
        // given
        String token = "test-token";
        User user = User.builder()
                .id("test")
                .username("test-user")
                .chats(new ArrayList<>())
                .build();
        Chat chat = Chat.builder()
                .id("test-id")
                .name("test-chat")
                .type(ChatType.PRIVATE_CHAT)
                .messages(new ArrayList<>())
                .members(new ArrayList<>())
                .build();

        // when
        when(userService.getUserByToken(token)).thenReturn(user);
        when(chatRepo.findById(chat.getId())).thenReturn(Optional.of(chat));

        // then
        assertThatThrownBy(() -> underTest.getChat(chat.getId(), token))
                .isInstanceOf(BadChatRequestException.class)
                .hasMessageContaining("Пользователь " + user.getUsername() + " не находится в данном чате");
    }

    @Test
    void canGetChatsByIds() {
        // given
        String token = "test-token";
        User user = User.builder()
                .id("test-id")
                .chats(new ArrayList<>())
                .build();
        Chat chat = Chat.builder()
                .id("test-chat-id")
                .name("test-name")
                .type(ChatType.PRIVATE_CHAT)
                .messages(new ArrayList<>())
                .members(List.of(user))
                .build();

        // when
        when(userService.getUserByToken(token)).thenReturn(user);
        when(chatRepo.findAllById(List.of(chat.getId()))).thenReturn(List.of(chat));
        List<ChatDto> expected = underTest.getChats(List.of(chat.getId()), token);

        // then
        verify(userService).getUserByToken(token);
        verify(chatRepo).findAllById(List.of(chat.getId()));
        assertThat(expected.get(0).getId()).isEqualTo(chat.getId());
        assertThat(expected.get(0).getName()).isEqualTo(chat.getName());
    }

    @Test
    void canConvertChatToDto() {
        // given
        Chat chat = Chat.builder()
                .id("test-id")
                .name("chat")
                .messages(new ArrayList<>())
                .members(new ArrayList<>())
                .type(ChatType.PRIVATE_CHAT)
                .build();

        // when
        ChatDto expected = underTest.convertToDto(chat);

        // then
        assertThat(expected.getName()).isEqualTo(chat.getName());
        assertThat(expected.getId()).isEqualTo(chat.getId());
    }

    @Test
    void canConvertGroupToDto() {
        // given
        User user = User.builder()
                .id("test-user-id")
                .chats(new ArrayList<>())
                .build();
        Group chat = Group.builder()
                .id("test-id")
                .name("chat")
                .messages(new ArrayList<>())
                .members(List.of(user))
                .owner(user)
                .administration(List.of(user))
                .type(ChatType.PRIVATE_CHAT)
                .build();

        // when
        ChatDto expected = underTest.convertToDto(chat);

        // then
        assertThat(expected.getName()).isEqualTo(chat.getName());
        assertThat(expected.getId()).isEqualTo(chat.getId());
        assertThat(expected.getOwner().getId()).isEqualTo(user.getId());
        assertThat(expected.getAdministration().get(0).getId()).isEqualTo(user.getId());
    }

    @Test
    void canFindChatById() {
        // given
        String chatId = "chat-id";
        Chat chat = Chat.builder()
                .id(chatId)
                .name("chat-name")
                .build();

        // when
        when(chatRepo.findById(chatId)).thenReturn(Optional.of(chat));
        Chat expected = underTest.findChat(chatId);

        // then
        verify(chatRepo).findById(chatId);
        assertThat(expected.getId()).isEqualTo(chat.getId());
        assertThat(expected.getName()).isEqualTo(chat.getName());
    }

    @Test
    void willThrowChatNotFound() {
        // given
        String id = "chat-id";

        // then
        assertThatThrownBy(() -> underTest.findChat(id))
                .isInstanceOf(ChatNotFoundException.class)
                .hasMessageContaining("Чат с id " + id + " не был найден");
    }
}