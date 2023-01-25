package com.practice.trixter.model;

import com.practice.trixter.dto.UserDto;
import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Message {
    @Id
    private String id;
    private String body;
    private UserDto sender;
    private String chatId;
}
