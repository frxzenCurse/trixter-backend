package com.practice.trixter.repo;

import com.practice.trixter.model.Chat;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
class ChatRepoTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ChatRepo underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldReturnChatByName() {
        // given
        String chatName = "chat-test";
        Chat chat = Chat.builder()
                .name(chatName)
                .build();
        mongoTemplate.insert(chat);

        // when
        Chat expected = underTest.findByName(chatName).orElse(null);

        // then
        assertThat(expected.getName()).isEqualTo(chatName);
    }
    @Test
    void itShouldNotReturnChatByName() {
        // given
        String chatName = "chat-test";

        // when
        Chat expected = underTest.findByName(chatName).orElse(null);

        // then
        assertThat(expected).isNull();
    }
}