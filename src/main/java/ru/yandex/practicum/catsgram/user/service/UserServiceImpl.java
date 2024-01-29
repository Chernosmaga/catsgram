package ru.yandex.practicum.catsgram.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.user.dto.UserDto;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;
import ru.yandex.practicum.catsgram.user.mapper.UserMapper;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserShortDto update(Long userId, UserDto user) {
        User thisUser = validation(userId, user);
        User updated = userRepository.save(thisUser);
        log.info("Обновлены данные о пользователе: {}", updated);
        return userMapper.toUserShortDto(updated);
    }

    @Override
    public UserDto getAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        log.info("Возвращены данные о владельце аккаунта: {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    public UserShortDto getById(Long userId) {
        User user = findUser(userId);
        log.info("Возвращены данные о пользователе: {}", user);
        return userMapper.toUserShortDto(user);
    }

    @Override
    public void deleteById(Long userId) {
        User user = findUser(userId);
        log.info("Пользователь деактивировал аккаунт: {}", user);
        userRepository.delete(user);
    }

    private User validation(Long userId, UserDto user) {
        User thisUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if (!user.getEmail().isEmpty()) {
            thisUser.setEmail(user.getEmail());
        }
        if (!user.getUsername().isEmpty()) {
            thisUser.setUsername(user.getUsername());
        }
        if (!user.getNickname().isEmpty()) {
            thisUser.setNickname(user.getNickname());
        }
        log.info("Данные о пользователе прошли валидацию: {}", thisUser);
        return thisUser;
    }

    private User findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}