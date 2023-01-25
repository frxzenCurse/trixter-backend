package com.practice.trixter.repo;

import com.practice.trixter.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRepo extends MongoRepository<Chat, String> {
    Optional<Chat> findByName(String name);
}
