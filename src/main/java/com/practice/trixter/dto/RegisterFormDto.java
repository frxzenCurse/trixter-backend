package com.practice.trixter.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterFormDto {
    private String username;
    private String password;
}
