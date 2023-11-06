package ru.yandex.practicum.catsgram.impl.dao;

import ru.yandex.practicum.catsgram.model.User;

import java.util.Optional;

public interface UserDao {
    User create(User user);
    Optional<User> findUserById(String id);
}
