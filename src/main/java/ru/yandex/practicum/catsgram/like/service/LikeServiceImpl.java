package ru.yandex.practicum.catsgram.like.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.like.model.Like;
import ru.yandex.practicum.catsgram.like.repository.LikeRepository;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
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
public class LikeServiceImpl implements LikeService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;
    private final UserMapper userMapper;

    @Override
    public void like(Long userId, Long postId) {
        User user = findUser(userId);
        Post post = findPost(postId);
        if (likeRepository.existsByUserAndPost(user, post)) {
            throw new AccessException("Вы уже поставили лайк посту");
        }
        setLikes(post, true);
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        like.setTime(LocalDateTime.now());
        likeRepository.save(like);
        log.info("Пользователь {} поставил лайк посту {}", user, post);
    }

    @Override
    public void dislike(Long userId, Long postId) {
        User user = findUser(userId);
        Post post = findPost(postId);
        if (!likeRepository.existsByUserAndPost(user, post)) {
            throw new AccessException("Вы не ставили лайк посту");
        }
        setLikes(post, false);
        likeRepository.delete(likeRepository.findByUserAndPost(user, post));
        log.info("Пользователь {} удалил лайк посту {}", user, post);
    }

    @Override
    public List<UserShortDto> getLikedUsers(Long userId, Long postId, int from, int size) {
        PageRequest page = PageRequest.of(from, size, DESC, "time");
        User user = findUser(userId);
        Post post = findPost(postId);
        if (!post.getAuthor().getId().equals(userId)) {
            throw new AccessException("Нет доступа");
        }
        List<UserShortDto> users = likeRepository.findAllByPost(post, page)
                .stream().map(Like::getUser).map(userMapper::toUserShortDto).collect(Collectors.toList());
        log.info("Пользователь {} вернул список лайкнувших пост: {}", user, users);
        return users;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Пост не найден"));
    }

    private void setLikes(Post post, Boolean isLiked) {
        long likes = post.getLikes();
        post.setLikes(isLiked ? likes + 1 : likes - 1);
        postRepository.save(post);
    }
}
