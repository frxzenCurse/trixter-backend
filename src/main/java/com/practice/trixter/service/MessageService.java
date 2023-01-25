package com.practice.trixter.service;

import com.practice.trixter.dto.MessageDto;
import com.practice.trixter.model.Message;
import com.practice.trixter.repo.MessageRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepo messageRepo;

    public Message save(Message message) {
        return messageRepo.insert(message);
    }
}
