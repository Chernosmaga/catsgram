package ru.yandex.practicum.catsgram.feed.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.enums.DateSort;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.mapper.PostMapper;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.subscription.model.Subscription;
import ru.yandex.practicum.catsgram.subscription.repository.SubscriptionRepository;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.yandex.practicum.catsgram.enums.DateSort.ALL_TIME;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PostMapper postMapper;
    private final static LocalDateTime TODAY = LocalDate.now().atStartOfDay();

    @Override
    public List<PostDto> getFeed(Long userId, int from, int size) {
        PageRequest page = PageRequest.of(from, size, DESC, "creationDate");
        User user = findUser(userId);
        List<PostDto> posts = postRepository.findAllByAuthorIsIn(subscriptionRepository
                .findAllByFollower(user, PageRequest.of(0, 10))
                .stream().map(Subscription::getAuthor).collect(Collectors.toList()), page)
                .stream().map(postMapper::toPostDto).collect(Collectors.toList());
        log.info("Пользователь {} запросил список постов: {}", user, posts);
        return posts;
    }

    @Override
    public List<PostDto> getWorldwidePopular(Long userId, DateSort sort, int from, int size) {
        PageRequest page = PageRequest.of(from, size);
        User user = findUser(userId);
        Page<Post> pages = ALL_TIME.equals(sort) ? postRepository.findWorldwidePopularAllTime(page)
                : postRepository.findWorldwidePopularToday(TODAY, page);
        List<PostDto> posts = pages.stream().map(postMapper::toPostDto).collect(Collectors.toList());
        log.info("Пользователь {} вернул список популярных постов: {}", user, posts);
        return posts;
    }

    @Override
    public List<PostDto> getFollowingPopular(Long userId, DateSort sort, int from, int size) {
        PageRequest page = PageRequest.of(from, size);
        User user = findUser(userId);
        List<User> users = subscriptionRepository.findAllByFollower(user)
                .stream().map(Subscription::getAuthor).collect(Collectors.toList());
        Page<Post> pages = ALL_TIME.equals(sort) ? postRepository.findFollowingPopularAllTime(users, page)
                : postRepository.findFollowingPopularToday(users, TODAY, page);
        List<PostDto> posts = pages.stream().map(postMapper::toPostDto).collect(Collectors.toList());
        log.info("Пользователь {} вернул список популярных постов среди подписок: {}", user, posts);
        return posts;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
