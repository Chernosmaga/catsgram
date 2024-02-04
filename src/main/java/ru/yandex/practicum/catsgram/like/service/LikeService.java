package ru.yandex.practicum.catsgram.like.service;

import ru.yandex.practicum.catsgram.user.dto.UserShortDto;

import java.util.List;

public interface LikeService {
    void like(Long userId, Long postId);

    void dislike(Long userId, Long postId);

    List<UserShortDto> getLikedUsers(Long userId, Long postId, int from, int size);
}
