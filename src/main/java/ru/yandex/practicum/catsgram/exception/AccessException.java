package ru.yandex.practicum.catsgram.exception;

public class AccessException extends RuntimeException {
    public AccessException(String message) {
        super(message);
    }
}