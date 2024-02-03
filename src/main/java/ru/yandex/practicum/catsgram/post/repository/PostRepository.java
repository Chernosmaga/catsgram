package ru.yandex.practicum.catsgram.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.user.model.User;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByAuthor(User author, Pageable page);
    Page<Post> findAllByAuthorIsIn(List<User> authors, Pageable page);
}
