package com.practice.trixter.repo;

import com.practice.trixter.model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataMongoTest
class UserRepoTest {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepo underTest;

    @AfterEach
    void tearDown() {
        underTest.deleteAll();
    }

    @Test
    void itShouldReturnUserByUsername() {
        // given
        User user = User.builder()
                .username("test-user")
                .status("test-status")
                .build();
        user = mongoTemplate.insert(user);

        // when
        User expected = underTest.findByUsername(user.getUsername()).orElse(null);

        // then
        assertThat(expected.getUsername()).isEqualTo(user.getUsername());
        assertThat(expected.getStatus()).isEqualTo(user.getStatus());
    }

    @Test
    void itShouldNotReturnUserByUsername() {
        // given
        String username = "test-user";

        // when
        User expected = underTest.findByUsername(username).orElse(null);

        // then
        assertThat(expected).isNull();
    }
}