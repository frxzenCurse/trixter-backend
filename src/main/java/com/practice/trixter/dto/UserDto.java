package com.practice.trixter.dto;

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
    private List<String> chatsIds;
}
