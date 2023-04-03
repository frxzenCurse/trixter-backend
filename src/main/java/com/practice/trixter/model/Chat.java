package com.practice.trixter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.trixter.enums.ChatType;
import lombok.*;
import lombok.experimental.SuperBuilder;
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
@SuperBuilder
@Document(collection = "chat")
public class Chat {
    @Id
    private String id;
    private String name;
    @DBRef
    @JsonIgnore
    private List<User> members = new ArrayList<>();
    @DBRef
    private List<Message> messages = new ArrayList<>();

    private ChatType type;

    public void addMessage(Message message) {
        messages.add(message);
    }
    public void addMember(User user) {
        members.add(user);
    }

    public List<String> getMembersIds() {
        return members.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }
}
