package com.practice.trixter.model;

import com.practice.trixter.dto.MessageDto;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "message")
public class Message {
    @Id
    private String id;
    private String body;
    @DBRef
    private User sender;
    private String chatId;

    public MessageDto convertToDto() {
        return MessageDto.builder()
                .id(id)
                .body(body)
                .sender(sender.convertToDto())
                .chatId(chatId)
                .build();
    }
}
