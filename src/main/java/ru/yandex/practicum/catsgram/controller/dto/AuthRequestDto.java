package ru.yandex.practicum.catsgram.controller.dto;

import lombok.Data;

@Data
public class AuthRequestDto {
    private String email;
    private String password;
}
