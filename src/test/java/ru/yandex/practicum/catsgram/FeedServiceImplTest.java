package ru.yandex.practicum.catsgram;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.catsgram.feed.service.FeedService;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.subscription.model.Subscription;
import ru.yandex.practicum.catsgram.subscription.repository.SubscriptionRepository;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.catsgram.enums.DateSort.ALL_TIME;
import static ru.yandex.practicum.catsgram.enums.DateSort.TODAY;
import static ru.yandex.practicum.catsgram.enums.Role.USER;
import static ru.yandex.practicum.catsgram.enums.Status.ACTIVE;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FeedServiceImplTest {
    private final FeedService feedService;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PostRepository postRepository;
    private final User author = new User(null, "user@mail.ru", "username", "username",
            "user password", USER, ACTIVE, LocalDateTime.now().minusWeeks(1), false);
    private final User follower = new User(null, "follower@mail.ru", "follower", "follower",
            "follower password", USER, ACTIVE, LocalDateTime.now().minusWeeks(2), false);

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        subscriptionRepository.deleteAll();
        postRepository.deleteAll();
    }

    @Test
    void getFeed_shouldReturnFeed() {
        User thisAuthor = userRepository.save(author);
        User thisFollower = userRepository.save(follower);
        subscriptionRepository
                .save(new Subscription(null, thisAuthor, thisFollower, LocalDateTime.now().minusDays(1),
                        true));
        postRepository.save(new Post(null, thisAuthor, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 0L));
        postRepository
                .save(new Post(null, thisAuthor, "second post", "https://pics.link",
                        LocalDateTime.now().minusHours(2), 0L));
        List<PostDto> feed = feedService.getFeed(thisFollower.getId(), 0, 10);

        assertNotNull(feed);
        assertEquals(2, feed.size());
    }

    @Test
    void getFeed_shouldReturnEmptyListIfNotFollowing() {
        User thisAuthor = userRepository.save(author);
        User thisFollower = userRepository.save(follower);
        postRepository.save(new Post(null, thisAuthor, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 0L));
        postRepository
                .save(new Post(null, thisAuthor, "second post", "https://pics.link",
                        LocalDateTime.now().minusHours(2), 0L));
        List<PostDto> feed = feedService.getFeed(thisFollower.getId(), 0, 10);

        assertTrue(feed.isEmpty());
    }

    @Test
    void getWorldwidePopular_shouldReturnWorldwidePopularPostsAllTime() {
        User thisAuthor = userRepository.save(author);
        User thisFollower = userRepository.save(follower);
        postRepository.save(new Post(null, thisAuthor, "first post",
                "https://pic.link", LocalDateTime.now().minusDays(4), 10L));
        postRepository
                .save(new Post(null, thisAuthor, "second post", "https://pics.link",
                        LocalDateTime.now().minusDays(2), 2L));
        List<PostDto> posts = feedService.getWorldwidePopular(thisFollower.getId(), ALL_TIME, 0, 10);

        assertFalse(posts.isEmpty());
        assertEquals(2, posts.size());
    }

    @Test
    void getWorldwidePopular_shouldReturnWorldwidePopularPostsToday() {
        User thisAuthor = userRepository.save(author);
        User thisFollower = userRepository.save(follower);
        postRepository.save(new Post(null, thisAuthor, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 10L));
        postRepository
                .save(new Post(null, thisAuthor, "second post", "https://pics.link",
                        LocalDateTime.now().minusHours(2), 2L));
        List<PostDto> posts = feedService.getWorldwidePopular(thisFollower.getId(), TODAY, 0, 10);

        assertFalse(posts.isEmpty());
        assertEquals(2, posts.size());
    }

    @Test
    void getFollowingPopularPostsAllTime() {
        User thisAuthor = userRepository.save(author);
        User thisFollower = userRepository.save(follower);
        postRepository.save(new Post(null, thisAuthor, "first post",
                "https://pic.link", LocalDateTime.now().minusDays(4), 10L));
        postRepository
                .save(new Post(null, thisAuthor, "second post", "https://pics.link",
                        LocalDateTime.now().minusDays(2), 2L));
        subscriptionRepository
                .save(new Subscription(null, thisAuthor, thisFollower, LocalDateTime.now().minusHours(2),
                        true));
        List<PostDto> posts = feedService.getFollowingPopular(thisFollower.getId(), ALL_TIME, 0, 10);

        assertFalse(posts.isEmpty());
        assertEquals(2, posts.size());
    }

    @Test
    void getFollowingPostsToday() {
        User thisAuthor = userRepository.save(author);
        User thisFollower = userRepository.save(follower);
        postRepository.save(new Post(null, thisAuthor, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4), 10L));
        postRepository
                .save(new Post(null, thisAuthor, "second post", "https://pics.link",
                        LocalDateTime.now().minusHours(2), 2L));
        subscriptionRepository
                .save(new Subscription(null, thisAuthor, thisFollower, LocalDateTime.now().minusHours(2),
                        true));
        List<PostDto> posts = feedService.getFollowingPopular(thisFollower.getId(), TODAY, 0, 10);

        assertFalse(posts.isEmpty());
        assertEquals(2, posts.size());
    }
}
