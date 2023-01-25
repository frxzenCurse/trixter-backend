package com.practice.trixter.model;

import com.practice.trixter.dto.UserDto;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    @Id
    private String id;
    private String username;
    private String password;
    private String status;
    private List<String> messageIds = new ArrayList<>();
    private List<String> chatsIds = new ArrayList<>();

    private FilesInfo avatar;

    public void addChatId(String id) {
        chatsIds.add(id);
    }
}
