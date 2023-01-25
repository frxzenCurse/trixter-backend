package com.practice.trixter.dto;

import com.practice.trixter.model.Chat;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String id;
    private String username;
    private String status;
    private String avatarUrl;
    private List<String> chatsId;
}
