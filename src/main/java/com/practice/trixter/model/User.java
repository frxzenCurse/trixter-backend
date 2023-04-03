package com.practice.trixter.model;

import com.practice.trixter.dto.UserDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String status;
    @DBRef
    private List<Chat> chats = new ArrayList<>();

    private FilesInfo avatar;

    public List<String> getChatsIds() {
        return chats.stream()
                .map(Chat::getId)
                .collect(Collectors.toList());
    }

    public void addChat(Chat chat) {
        chats.add(chat);
    }

    public UserDto convertToDto() {
        UserDto userDto = UserDto.builder()
                .id(id)
                .username(username)
                .chatsIds(getChatsIds())
                .status(status)
                .build();

        if (avatar != null) {
            userDto.setAvatarUrl(avatar.getUrl());
        }

        return userDto;
    }
}
