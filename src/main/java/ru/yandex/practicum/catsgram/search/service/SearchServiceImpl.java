package ru.yandex.practicum.catsgram.search.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.enums.PostSort;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.mapper.PostMapper;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.subscription.model.Subscription;
import ru.yandex.practicum.catsgram.subscription.repository.SubscriptionRepository;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;
import ru.yandex.practicum.catsgram.user.mapper.UserMapper;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PostMapper postMapper;
    private final UserMapper userMapper;

    @Override
    public List<PostDto> findPosts(Long userId, String text, PostSort sort, int from, int size) {
        User user = findUser(userId);
        PageRequest page = PageRequest.of(from, size);
        List<Post> pages = new ArrayList<>();
        switch (sort) {
            case DATE:
                pages = postRepository
                        .findAllByDescriptionContainingIgnoreCaseOrTagTextContainingIgnoreCaseOrderByCreationDateDesc(text,
                                text, page).stream().collect(Collectors.toList());
                break;
            case FRIENDS:
                pages = postRepository
                        .findAllByAuthorIsInAndDescriptionContainingIgnoreCaseOrTagTextContainingIgnoreCaseOrderByCreationDateDesc(
                                subscriptionRepository.findAllByFollower(user)
                        .stream().map(Subscription::getAuthor).collect(Collectors.toList()), text, text, page).stream()
                        .collect(Collectors.toList());
                break;
            case POPULAR:
                pages = postRepository
                        .findAllByDescriptionContainingIgnoreCaseOrTagTextContainingIgnoreCaseOrderByLikesDesc(text,
                                text, page).stream().collect(Collectors.toList());
                break;
        }
        List<PostDto> posts = pages.stream().map(postMapper::toPostDto).collect(Collectors.toList());
        log.info("Возвращение результатов поиска пользователя {} постов: {}", user, posts);
        return posts;
    }

    @Override
    public List<UserShortDto> findUsers(Long userId, String text, int from, int size) {
        User user = findUser(userId);
        PageRequest page = PageRequest.of(from, size);
        List<UserShortDto> users = userRepository.findAllByNicknameContainingIgnoreCase(text, page).stream()
                .filter(owner -> !owner.getUsername().equals(user.getUsername()))
                .map(userMapper::toUserShortDto).collect(Collectors.toList());
        log.info("Возвращены результаты поиска пользователя {} пользователей: {}", user, users);
        return users;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
