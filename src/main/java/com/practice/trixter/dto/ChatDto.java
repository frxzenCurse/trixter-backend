package com.practice.trixter.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatDto {
    private String id;
    private String name;
    private String type;
    private UserDto owner;
    private List<UserDto> administration = new ArrayList<>();
    private List<UserDto> members = new ArrayList<>();
    private List<MessageDto> messages = new ArrayList<>();
}
