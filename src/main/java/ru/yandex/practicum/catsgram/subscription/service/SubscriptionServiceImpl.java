package ru.yandex.practicum.catsgram.subscription.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.subscription.model.Subscription;
import ru.yandex.practicum.catsgram.subscription.repository.SubscriptionRepository;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;
import ru.yandex.practicum.catsgram.user.mapper.UserMapper;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserMapper userMapper;

    @Override
    public void follow(Long followerId, Long userId) {
        User user = findUser(userId);
        User follower = findUser(followerId);
        if (subscriptionRepository.existsByAuthorAndFollower(user, follower)) {
            throw new AccessException("Вы подписаны на пользователя");
        }
        Subscription subscription = new Subscription();
        subscription.setAuthor(user);
        subscription.setFollower(follower);
        subscription.setSubscriptionTime(LocalDateTime.now());
        subscriptionRepository.save(subscription);
        log.info("Пользователь {} подписался на пользователя {}", follower, user);
    }

    @Override
    public void unfollow(Long followerId, Long userId) {
        User user = findUser(userId);
        User follower = findUser(followerId);
        if (!subscriptionRepository.existsByAuthorAndFollower(user, follower)) {
            throw new AccessException("Вы не подписаны на пользователя");
        }
        subscriptionRepository.delete(subscriptionRepository.findByAuthorAndFollower(user, follower));
        log.info("Пользователь {} отписался от пользователя {}", follower, user);
    }

    @Override
    public List<UserShortDto> getFollowers(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from, size, DESC, "subscriptionTime");
        User user = findUser(userId);
        List<UserShortDto> subscribers = subscriptionRepository.findAllByAuthor(user, page)
                .stream().map(subscription -> findUser(subscription.getFollower().getId()))
                .map(userMapper::toUserShortDto).collect(Collectors.toList());
        log.info("Пользователь вернул список подписчиков: {}", subscribers);
        return subscribers;
    }

    @Override
    public List<UserShortDto> getFollowing(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from, size, DESC, "subscriptionTime");
        User user = findUser(userId);
        List<UserShortDto> subscriptions = subscriptionRepository.findAllByFollower(user, page).stream()
                .map(subscription -> findUser(subscription.getAuthor().getId()))
                .map(userMapper::toUserShortDto).collect(Collectors.toList());
        log.info("Пользователь вернул список подписок: {}", subscriptions);
        return subscriptions;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
