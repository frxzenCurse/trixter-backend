package com.practice.trixter.exceptions;

public class BadChatRequestException extends RuntimeException {
    public BadChatRequestException(String message) {
        super(message);
    }
}
