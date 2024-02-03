package ru.yandex.practicum.catsgram;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.catsgram.feed.dto.FeedDto;
import ru.yandex.practicum.catsgram.feed.service.FeedService;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.subscription.model.Subscription;
import ru.yandex.practicum.catsgram.subscription.repository.SubscriptionRepository;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
            "user password", USER, ACTIVE, LocalDateTime.now().minusDays(3));
    private final User follower = new User(null, "follower@mail.ru", "follower", "follower",
            "follower password", USER, ACTIVE, LocalDateTime.now().minusDays(2));

    @Test
    void getFeed_shouldReturnFeed() {
        User thisAuthor = userRepository.save(author);
        User thisFollower = userRepository.save(follower);
        subscriptionRepository
                .save(new Subscription(null, thisAuthor, thisFollower, LocalDateTime.now().minusDays(1)));
        postRepository.save(new Post(null, thisAuthor, "first post",
                "https://pic.link", LocalDateTime.now().minusHours(4)));
        postRepository
                .save(new Post(null, thisAuthor, "second post", "https://pics.link",
                        LocalDateTime.now().minusHours(2)));
        FeedDto feed = feedService.getFeed(thisFollower.getId(), 0, 10);

        assertNotNull(feed);
        assertEquals(2, feed.getPosts().size());
    }
}
