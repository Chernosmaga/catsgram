package ru.yandex.practicum.catsgram.subscription.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.subscription.model.Subscription;
import ru.yandex.practicum.catsgram.user.model.User;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    boolean existsByAuthorAndFollower(User author, User follower);

    Subscription findByAuthorAndFollower(User author, User follower);

    Page<Subscription> findAllByAuthor(User author, Pageable page);

    Page<Subscription> findAllByFollower(User follower, Pageable page);

    List<Subscription> findAllByFollower(User follower);
}
