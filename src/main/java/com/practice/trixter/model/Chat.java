package com.practice.trixter.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Chat {
    @Id
    private String id;
    private String name;
    private List<String> membersId = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message message) {
        messages.add(message);
    }
    public void addMember(User user) {
        membersId.add(user.getId());
    }
}
