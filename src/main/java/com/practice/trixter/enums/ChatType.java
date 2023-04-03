package com.practice.trixter.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatType {
    CHANNEL("channel"),
    PRIVATE_CHAT("private chat"),
    GROUP("group");

    private final String type;
}
