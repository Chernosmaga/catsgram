package ru.yandex.practicum.catsgram.comment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.catsgram.comment.model.Comment;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.user.model.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByPost(Post post);

    void deleteAllByPost(Post post);

    Page<Comment> findAllByAuthor(User author, Pageable pageable);
}
