package com.practice.trixter.exceptions;

public class BadRegisterRequestException extends RuntimeException {
    public BadRegisterRequestException(String message) {
        super(message);
    }
}
