package com.practice.trixter.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDto {
    private String id;
    private String body;
    private UserDto sender;
    private String chatId;
}
