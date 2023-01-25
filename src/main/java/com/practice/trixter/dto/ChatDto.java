package com.practice.trixter.dto;

import com.practice.trixter.model.Message;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDto {
    @Id
    private String id;
    private String name;
    private List<UserDto> members = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();
}
