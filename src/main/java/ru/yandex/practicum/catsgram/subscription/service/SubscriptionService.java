package ru.yandex.practicum.catsgram.subscription.service;

import ru.yandex.practicum.catsgram.user.dto.UserShortDto;

import java.util.List;

public interface SubscriptionService {
    void follow(Long followerId, Long userId);

    void unfollow(Long followerId, Long userId);

    void approveFollowing(Long userId, Long requesterId, Boolean isApproved);

    List<UserShortDto> getRequesters(Long userId, int from, int size);

    List<UserShortDto> getFollowers(Long userId, int from, int size);

    List<UserShortDto> getFollowing(Long userId, int from, int size);
}
