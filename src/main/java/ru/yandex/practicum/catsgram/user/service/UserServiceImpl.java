package ru.yandex.practicum.catsgram.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.user.dto.UserDto;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;
import ru.yandex.practicum.catsgram.user.mapper.UserMapper;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserShortDto update(Long userId, UserDto user) {
        User thisUser = validation(user);
        if (!userRepository.existsById(userId) && !thisUser.getId().equals(userId)) {
            throw new AccessException("Нет доступа");
        }
        return userMapper.toUserShortDto(userRepository.save(thisUser));
    }

    @Override
    public UserDto getAccount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userMapper.toUserDto(user);
    }

    @Override
    public UserShortDto getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return userMapper.toUserShortDto(user);
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.deleteById(userId);
    }

    private User validation(UserDto user) {
        User thisUser = userRepository.findById(user.getId())
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
        return thisUser;
    }
}