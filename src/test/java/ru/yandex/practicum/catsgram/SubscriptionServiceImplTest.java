package ru.yandex.practicum.catsgram;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.subscription.repository.SubscriptionRepository;
import ru.yandex.practicum.catsgram.subscription.service.SubscriptionService;
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
public class SubscriptionServiceImplTest {
    private final SubscriptionService subscriptionService;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;
    private final User user = new User(null, "user@mail.ru", "username", "username",
            "user password", USER, ACTIVE, LocalDateTime.now().minusDays(2));
    private final User follower = new User(null, "follower@mail.ru", "follower", "follower",
            "follower password", USER, ACTIVE, LocalDateTime.now().minusDays(1));

    @AfterEach
    void afterEach() {
        userRepository.deleteAll();
        subscriptionRepository.deleteAll();
    }

    @Test
    void follow_shouldFollowUser() {
        User author = userRepository.save(user);
        User thisFollower = userRepository.save(follower);
        subscriptionService.follow(thisFollower.getId(), author.getId());

        assertFalse(subscriptionService.getFollowers(author.getId(), 0, 10).isEmpty());
    }

    @Test
    void follow_shouldThrowExceptionIfAlreadyFollowing() {
        User author = userRepository.save(user);
        User thisFollower = userRepository.save(follower);
        subscriptionService.follow(thisFollower.getId(), author.getId());

        assertThrows(AccessException.class,
                () -> subscriptionService.follow(thisFollower.getId(), author.getId()));
    }

    @Test
    void unfollow_shouldUnfollowUser() {
        User author = userRepository.save(user);
        User thisFollower = userRepository.save(follower);
        subscriptionService.follow(thisFollower.getId(), author.getId());
        subscriptionService.unfollow(thisFollower.getId(), author.getId());

        assertTrue(subscriptionService.getFollowers(author.getId(), 0, 10).isEmpty());
        assertTrue(subscriptionService.getFollowing(follower.getId(), 0, 10).isEmpty());
    }

    @Test
    void unfollow_shouldThrowExceptionIfNotFollowing() {
        User author = userRepository.save(user);
        User thisFollower = userRepository.save(follower);
        subscriptionService.follow(thisFollower.getId(), author.getId());
        subscriptionService.unfollow(thisFollower.getId(), author.getId());

        assertThrows(AccessException.class,
                () -> subscriptionService.unfollow(thisFollower.getId(), author.getId()));
    }

    @Test
    void getFollowers_shouldReturnListOfFollowers() {
        User author = userRepository.save(user);
        User thisFollower = userRepository.save(follower);
        subscriptionService.follow(thisFollower.getId(), author.getId());
        List<UserShortDto> followers = subscriptionService.getFollowers(author.getId(), 0, 10);

        assertFalse(followers.isEmpty());
        assertEquals(1, followers.size());
    }

    @Test
    void getFollowing_shouldReturnListOfFollowing() {
        User author = userRepository.save(user);
        User thisFollower = userRepository.save(follower);
        subscriptionService.follow(thisFollower.getId(), author.getId());
        List<UserShortDto> following = subscriptionService.getFollowing(thisFollower.getId(), 0, 10);

        assertFalse(following.isEmpty());
        assertEquals(1, following.size());
    }
}
