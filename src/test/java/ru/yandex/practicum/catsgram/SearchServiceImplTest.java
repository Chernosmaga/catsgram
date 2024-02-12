package ru.yandex.practicum.catsgram;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.search.service.SearchService;
import ru.yandex.practicum.catsgram.subscription.service.SubscriptionService;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.catsgram.enums.PostSort.*;
import static ru.yandex.practicum.catsgram.enums.Role.USER;
import static ru.yandex.practicum.catsgram.enums.Status.ACTIVE;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SearchServiceImplTest {
    private final SearchService searchService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final SubscriptionService subscriptionService;
    private final User user = new User(null, "user@mail.ru", "username", "nickname",
            "password", USER, ACTIVE, LocalDateTime.now().minusDays(2), false);
    private final User secondUser = new User(null, "second@mail.ru", "second username",
            "second nickname", "second password", USER, ACTIVE, LocalDateTime.now().minusDays(1),
            false);
    private final User thirdUser = new User(null, "third@mail.ru", "third username",
            "third nickname", "third password", USER, ACTIVE, LocalDateTime.now().minusDays(3),
            false);

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void findPosts_shouldReturnListOfPostsWithDateSort() {
        User firstUser = userRepository.save(user);
        User thisSecondUser = userRepository.save(secondUser);
        postRepository.save(new Post(null, firstUser, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 0L));
        postRepository
                .save(new Post(null, firstUser, "second post", "https://pics.link",
                        LocalDateTime.now().minusHours(2), 0L));
        List<PostDto> posts = searchService.findPosts(thisSecondUser.getId(), "post", DATE, 0, 10);

        assertFalse(posts.isEmpty());
        assertEquals(2, posts.size());
    }

    @Test
    void findPosts_shouldReturnListOfPostsWithFriendsSort() {
        User firstUser = userRepository.save(user);
        User thisSecondUser = userRepository.save(secondUser);
        subscriptionService.follow(firstUser.getId(), thisSecondUser.getId());
        subscriptionService.follow(thisSecondUser.getId(), firstUser.getId());
        postRepository.save(new Post(null, firstUser, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 0L));
        postRepository
                .save(new Post(null, firstUser, "second post", "https://pics.link",
                        LocalDateTime.now().minusHours(2), 0L));
        List<PostDto> posts = searchService.findPosts(thisSecondUser.getId(), "post", FRIENDS, 0, 10);

        assertFalse(posts.isEmpty());
        assertEquals(2, posts.size());
    }

    @Test
    void findPosts_shouldReturnListOfPostsWithPopularSort() {
        User firstUser = userRepository.save(user);
        User thisSecondUser = userRepository.save(secondUser);
        postRepository.save(new Post(null, firstUser, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 10L));
        postRepository
                .save(new Post(null, firstUser, "second post", "https://pics.link",
                        LocalDateTime.now().minusHours(2), 2L));
        List<PostDto> posts = searchService.findPosts(thisSecondUser.getId(), "post", POPULAR, 0, 10);

        assertFalse(posts.isEmpty());
        assertEquals(2, posts.size());
    }

    @Test
    void findPosts_shouldReturnSortedListIfTextIsNull() {
        User firstUser = userRepository.save(user);
        User thisSecondUser = userRepository.save(secondUser);
        postRepository.save(new Post(null, firstUser, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 10L));
        postRepository
                .save(new Post(null, firstUser, "second post", "https://pics.link",
                        LocalDateTime.now().minusHours(2), 2L));
        List<PostDto> posts = searchService.findPosts(thisSecondUser.getId(), " ", POPULAR, 0, 10);

        assertFalse(posts.isEmpty());
        assertEquals(2, posts.size());
    }

    @Test
    void findUsers_shouldReturnListOfUsers() {
        User firstUser = userRepository.save(user);
        userRepository.save(secondUser);
        userRepository.save(thirdUser);
        List<UserShortDto> users = searchService.findUsers(firstUser.getId(), "nickname", 0, 10);

        assertFalse(users.isEmpty());
        assertEquals(2, users.size());
    }

    @Test
    void findUsers_shouldReturnEmptyListIfTextIsNull() {
        User firstUser = userRepository.save(user);
        userRepository.save(secondUser);
        userRepository.save(thirdUser);
        List<UserShortDto> users = searchService.findUsers(firstUser.getId(), " ", 0, 10);

        assertTrue(users.isEmpty());
    }
}
