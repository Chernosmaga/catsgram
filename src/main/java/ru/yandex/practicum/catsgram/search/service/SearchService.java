package ru.yandex.practicum.catsgram.search.service;

import ru.yandex.practicum.catsgram.enums.PostSort;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;

import java.util.List;

public interface SearchService {
    List<PostDto> findPosts(Long userId, String text, PostSort sort, int from, int size);

    List<UserShortDto> findUsers(Long userId, String text, int from, int size);
}
