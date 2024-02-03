package ru.yandex.practicum.catsgram.feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.feed.dto.FeedDto;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.mapper.PostMapper;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.subscription.model.Subscription;
import ru.yandex.practicum.catsgram.subscription.repository.SubscriptionRepository;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PostMapper postMapper;

    @Override
    public FeedDto getFeed(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from, size, DESC, "creationDate");
        User user = findUser(userId);
        List<PostDto> posts = postRepository.findAllByAuthorIsIn(subscriptionRepository
                .findAllByFollower(user, PageRequest.of(0, 10))
                .stream().map(Subscription::getAuthor).collect(Collectors.toList()), page)
                .stream().map(postMapper::toPostDto).collect(Collectors.toList());
        FeedDto feedDto = new FeedDto();
        feedDto.setDate(LocalDateTime.now());
        feedDto.setPosts(posts);
        log.info("Пользователь {} запросил список постов: {}", user, posts);
        return feedDto;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
