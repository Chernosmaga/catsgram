package ru.yandex.practicum.catsgram.impl.dao;

import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.util.Collection;

public interface PostDao {
    Post create(Post post);
    Collection<Post> findPostsByUser(User user);
}
