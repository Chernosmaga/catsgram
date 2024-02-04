package ru.yandex.practicum.catsgram.like.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.like.model.Like;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.user.model.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Like findByUserAndPost(User user, Post post);

    boolean existsByUserAndPost(User user, Post post);

    Page<Like> findAllByPost(Post post, Pageable pageable);
}
