package ru.yandex.practicum.catsgram.exception.model;

import lombok.Data;

@Data
public class ErrorResponse {
    private final String error;
}
