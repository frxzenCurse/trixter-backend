package com.practice.trixter.service;

import com.practice.trixter.dto.RegisterFormDto;
import com.practice.trixter.dto.UserDto;
import com.practice.trixter.exceptions.BadRegisterRequestException;
import com.practice.trixter.exceptions.UserNotFoundException;
import com.practice.trixter.model.Chat;
import com.practice.trixter.model.User;
import com.practice.trixter.repo.ChatRepo;
import com.practice.trixter.repo.UserRepo;
import com.practice.trixter.util.JWTUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepo userRepo;
    @Mock private ChatRepo chatRepo;
    @Mock private BCryptPasswordEncoder passwordEncoder;
    @Mock private BlobStorageService blobStorageService;

    private UserService underTest;

    @BeforeEach
    void setUp() {
        underTest = new UserService(userRepo, chatRepo, passwordEncoder, blobStorageService);
    }

    @Test
    void canRegisterUser() {
        // given
        RegisterFormDto form = RegisterFormDto.builder()
                .username("test-user")
                .password("test-password")
                .build();
        Chat chat = Chat.builder()
                .name("test-chat")
                .members(new ArrayList<>())
                .build();
        User user = User.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .status("tests-stat")
                .chats(List.of(chat))
                .build();

        // when
        when(chatRepo.findByName("general")).thenReturn(Optional.of(chat));
        when(userRepo.insert(any(User.class))).thenReturn(user);

        UserDto expected = underTest.register(form);

        // then
        verify(chatRepo).save(chat);

        assertThat(expected.getUsername()).isEqualTo(form.getUsername());
    }

    @Test
    void willThrowRegisterException() {
        // given
        RegisterFormDto form = RegisterFormDto.builder()
                .username("test-user")
                .password("test-password")
                .build();
        User user = User.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .status("tests-stat")
                .build();

        // when
        when(userRepo.findByUsername(form.getUsername())).thenReturn(Optional.of(user));

        // then
        assertThatThrownBy(() -> underTest.register(form))
                .isInstanceOf(BadRegisterRequestException.class)
                .hasMessageContaining("Пользователь с никнеймом " + form.getUsername() + " уже существует");
    }

    @Test
    void canGetUser() {
        // given
        Chat chat = Chat.builder()
                .name("test")
                .build();
        User user = User.builder()
                .username("test-user")
                .status("test-status")
                .chats(List.of(chat))
                .build();
        String token = "Bearer " + JWTUtil.createAccessToken(user, "test");

        // when
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user));
        UserDto expected = underTest.getUser(token);

        // then
        assertThat(expected.getUsername()).isEqualTo(user.getUsername());
        assertThat(expected.getStatus()).isEqualTo(user.getStatus());
    }

    @Test
    void canSaveUser() {
        // given
        User user = User.builder()
                .username("test-username")
                .password("test-pass")
                .build();

        // when
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encoded-test");
        underTest.save(user);

        // then
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        verify(passwordEncoder).encode(any());
        verify(userRepo).insert(argumentCaptor.capture());

        User expected = argumentCaptor.getValue();

        assertThat(expected.getUsername()).isEqualTo(user.getUsername());
        assertThat(expected.getPassword()).isEqualTo("encoded-test");
    }

    @Test
    void canFindUserById() {
        // given
        String id = "test-id";
        User user = User.builder()
                .id(id)
                .username("test")
                .build();

        // when
        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        User expected = underTest.find(id);

        // then
        verify(userRepo).findById(id);
        assertThat(expected.getId()).isEqualTo(id);
    }

    @Test
    void willThrowWhenUserNotFoundById() {
        // given
        String id = "test-id";

        // when, then
        assertThatThrownBy(() -> underTest.find(id))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь не был найден");
    }

    @Test
    void canFindUserByUsername() {
        // given
        String username = "test-user";
        User user = User.builder()
                .username(username)
                .build();

        // when
        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        User expected = underTest.findByUsername(username);

        // then
        assertThat(expected.getUsername()).isEqualTo(username);
    }

    @Test
    void willThrowUserNotFoundByUsername() {
        // given
        String username = "test-username";

        // when, then
        assertThatThrownBy(() -> underTest.findByUsername(username))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessageContaining("Пользователь с никнеймом " + username + " не был найден");
    }

    @Test
    void canUpdateUser() {
        // given
        User user = User.builder()
                .username("username")
                .password("pass")
                .status("status")
                .build();

        // when
        when(userRepo.save(user)).thenReturn(user);
        User expected = underTest.update(user);

        // then
        verify(userRepo).save(user);
        assertThat(expected.getUsername()).isEqualTo(user.getUsername());
        assertThat(expected.getPassword()).isEqualTo(user.getPassword());
        assertThat(expected.getStatus()).isEqualTo(user.getStatus());
    }

    @Test
    void canUpdateUserStatus() {
        // given
        String id = "test-id";
        UserDto userDto = UserDto.builder()
                .id(id)
                .status("test-status")
                .build();
        User user = User.builder()
                .username("test-user")
                .build();

        // when
        when(userRepo.findById(id)).thenReturn(Optional.of(user));
        UserDto expected = underTest.update(userDto);

        // then
        assertThat(expected.getStatus()).isEqualTo(userDto.getStatus());
    }

    @Test
    @Disabled
    void canUpdateUserAvatar() {

    }

    @Test
    @Disabled
    void canLoadUserByUsername() {

    }

    @Test
    void canGetUserByToken() {
        // given
        User user = User.builder()
                .password("123")
                .username("test")
                .build();
        String token = "Bearer " + JWTUtil.createAccessToken(user, "test-q");

        // when
        when(userRepo.findByUsername(anyString())).thenReturn(Optional.of(user));
        User expected = underTest.getUserByToken(token);

        // then
        verify(userRepo).findByUsername(anyString());
        assertThat(expected.getUsername()).isEqualTo(user.getUsername());
    }
}