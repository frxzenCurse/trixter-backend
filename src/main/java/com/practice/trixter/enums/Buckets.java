package com.practice.trixter.enums;

import lombok.Getter;

@Getter
public enum Buckets {
    MESSAGE_FILES("trixter-message-files"),
    AVATARS("trixter-avatars");

    private final String bucketName;

    Buckets(String bucketName) {
        this.bucketName = bucketName;
    }
}
