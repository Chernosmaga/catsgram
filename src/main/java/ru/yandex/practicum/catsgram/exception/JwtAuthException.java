package ru.yandex.practicum.catsgram.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import javax.naming.AuthenticationException;

@Getter
public class JwtAuthException extends AuthenticationException {
    private HttpStatus status;

    public JwtAuthException(String explanation, HttpStatus status) {
        super(explanation);
        this.status = status;
    }

    public JwtAuthException(String explanation) {
        super(explanation);
    }
}
