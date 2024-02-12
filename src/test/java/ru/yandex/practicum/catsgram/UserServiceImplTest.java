package ru.yandex.practicum.catsgram;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.subscription.model.Subscription;
import ru.yandex.practicum.catsgram.subscription.repository.SubscriptionRepository;
import ru.yandex.practicum.catsgram.user.dto.UserDto;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;
import ru.yandex.practicum.catsgram.user.service.UserService;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.catsgram.enums.Role.USER;
import static ru.yandex.practicum.catsgram.enums.Status.ACTIVE;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {
    private final UserService userService;
    private final UserRepository repository;
    private final SubscriptionRepository subscriptionRepository;
    private final User user = new User(null, "user@mail.ru", "username", "nickname",
            "password", USER, ACTIVE, LocalDateTime.now().minusDays(1), false);
    private final User secondUser = new User(null, "second@mail.ru", "second", "second",
            "second password", USER, ACTIVE, LocalDateTime.now().minusDays(3), false);

    @AfterEach
    void afterEach() {
        repository.deleteAll();
    }

    @Test
    void update_shouldUpdateUsersData() {
        User saved = repository.save(user);
        UserDto updating = new UserDto(saved.getId(), "someone@mail.ru", "someone", "someone",
                false);
        UserShortDto updated = userService.update(saved.getId(), updating);
        User found = repository.findById(updated.getId()).orElseThrow(() -> new NotFoundException("Не найден"));

        assertThat(found.getEmail(), equalTo("someone@mail.ru"));
        assertThat(found.getNickname(), equalTo("someone"));
        assertThat(found.getNickname(), equalTo("someone"));
    }

    @Test
    void getAccount_shouldReturnOwnersData() {
        User saved = repository.save(user);
        UserDto returned = userService.getAccount(saved.getId());

        assertThat(returned.getId(), equalTo(saved.getId()));
        assertThat(returned.getEmail(), equalTo(saved.getEmail()));
        assertThat(returned.getUsername(), equalTo(saved.getUsername()));
        assertThat(returned.getNickname(), equalTo(saved.getNickname()));
    }

    @Test
    void getAccount_shouldThrowExceptionIfIncorrectId() {
        assertThrows(NotFoundException.class,
                () -> userService.getAccount(999L));
    }

    @Test
    void getById_shouldReturnUserData() {
        User saved = repository.save(user);
        User requester = repository.save(secondUser);
        subscriptionRepository.save(new Subscription(null, saved, requester, LocalDateTime.now().minusHours(1),
                true));
        UserShortDto returned = userService.getById(requester.getId(), saved.getId());

        assertThat(saved.getNickname(), equalTo(returned.getNickname()));
        assertThat(saved.getId(), equalTo(returned.getId()));
    }

    @Test
    void getById_shouldThrowExceptionIfNotFriendsAndGettingAccount() {
        User saved = repository.save(user);
        User requester = repository.save(secondUser);

        assertThrows(AccessException.class,
                () -> userService.getById(requester.getId(), saved.getId()));
    }

    @Test
    void getById_shouldThrowExceptionIfRequesterIdIsIncorrect() {
        assertThrows(NotFoundException.class,
                () -> userService.getById(999L, 999L));
    }

    @Test
    void getById_shouldThrowExceptionIfUserIdIsIncorrect() {
        User requester = repository.save(secondUser);

        assertThrows(NotFoundException.class,
                () -> userService.getById(requester.getId(), 999L));
    }

    @Test
    void deleteById_shouldDeleteUserData() {
        User saved = repository.save(user);
        userService.deleteById(saved.getId());

        assertThrows(NotFoundException.class,
                () -> userService.getAccount(saved.getId()));
    }

    @Test
    void deleteById_shouldThrowExceptionIfIdIsIncorrect() {
        assertThrows(NotFoundException.class,
                () -> userService.deleteById(999L));
    }
}
