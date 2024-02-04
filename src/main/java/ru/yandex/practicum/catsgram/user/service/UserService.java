package ru.yandex.practicum.catsgram.user.service;

import ru.yandex.practicum.catsgram.user.dto.UserDto;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;

public interface UserService {
    UserShortDto update(Long userId, UserDto user);

    UserDto getAccount(Long userId);

    UserShortDto getById(Long userId);

    void deleteById(Long userId);
}
