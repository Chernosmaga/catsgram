package ru.yandex.practicum.catsgram.feed.service;

import ru.yandex.practicum.catsgram.enums.DateSort;
import ru.yandex.practicum.catsgram.post.dto.PostDto;

import java.util.List;

public interface FeedService {
    List<PostDto> getFeed(Long userId, int from, int size);

    List<PostDto> getWorldwidePopular(Long userId, DateSort sort, int from, int size);

    List<PostDto> getFollowingPopular(Long userId, DateSort sort, int from, int size);
}
