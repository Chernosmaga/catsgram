package ru.yandex.practicum.catsgram.feed.service;

import ru.yandex.practicum.catsgram.feed.dto.FeedDto;

public interface FeedService {
    FeedDto getFeed(Long userId, int from, int size);
}
