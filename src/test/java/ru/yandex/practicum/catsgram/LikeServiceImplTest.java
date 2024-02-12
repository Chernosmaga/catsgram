package ru.yandex.practicum.catsgram;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.like.service.LikeService;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.catsgram.enums.Role.USER;
import static ru.yandex.practicum.catsgram.enums.Status.ACTIVE;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class LikeServiceImplTest {
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeService likeService;
    private final User first = new User(null, "user@mail.ru", "username", "username",
            "user password", USER, ACTIVE, LocalDateTime.now().minusDays(2), false);
    private final User second = new User(null, "follower@mail.ru", "follower", "follower",
            "follower password", USER, ACTIVE, LocalDateTime.now().minusDays(1), false);

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void like_shouldLikePostAndIncreaseLikes() {
        User firstUser = userRepository.save(first);
        User secondUser = userRepository.save(second);
        Post post = postRepository.save(new Post(null, firstUser, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 0L));
        likeService.like(secondUser.getId(), post.getId());
        Post returned = postRepository.findById(post.getId()).orElseThrow(() -> new NotFoundException("На найдено"));

        assertEquals(1, returned.getLikes());
    }

    @Test
    void like_shouldThrowExceptionIfPostAlreadyLikes() {
        User firstUser = userRepository.save(first);
        User secondUser = userRepository.save(second);
        Post post = postRepository.save(new Post(null, firstUser, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 0L));
        likeService.like(secondUser.getId(), post.getId());

        assertThrows(AccessException.class,
                () -> likeService.like(secondUser.getId(), post.getId()));
    }

    @Test
    void dislike_shouldDislikePost() {
        User firstUser = userRepository.save(first);
        User secondUser = userRepository.save(second);
        Post post = postRepository.save(new Post(null, firstUser, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 0L));
        likeService.like(secondUser.getId(), post.getId());
        likeService.dislike(secondUser.getId(), post.getId());

        assertEquals(0, post.getLikes());
    }

    @Test
    void dislike_shouldThrowExceptionIfPostWasNotLiked() {
        User firstUser = userRepository.save(first);
        User secondUser = userRepository.save(second);
        Post post = postRepository.save(new Post(null, firstUser, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 0L));

        assertThrows(AccessException.class,
                () -> likeService.dislike(secondUser.getId(), post.getId()));
    }

    @Test
    void getLikedUsers_shouldReturnListOfPeopleWhoLikedPost() {
        User firstUser = userRepository.save(first);
        User secondUser = userRepository.save(second);
        Post post = postRepository.save(new Post(null, firstUser, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 0L));
        likeService.like(secondUser.getId(), post.getId());
        List<UserShortDto> users = likeService.getLikedUsers(firstUser.getId(), post.getId(), 0, 10);

        assertFalse(users.isEmpty());
        assertEquals("follower", users.get(0).getNickname());
    }
}
