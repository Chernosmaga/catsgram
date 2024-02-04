package ru.yandex.practicum.catsgram.post.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findAllByAuthor(User author, Pageable page);

    Page<Post> findAllByAuthorIsIn(List<User> authors, Pageable page);

    @Query(value = "select * from posts as s order by s.likes desc", nativeQuery = true)
    Page<Post> findWorldwidePopularAllTime(Pageable page);

    @Query(value = "select * from posts as s where s.creation_date > ?1 order by s.likes", nativeQuery = true)
    Page<Post> findWorldwidePopularToday(LocalDateTime date, Pageable page);

    @Query(value = "select * from posts as s where s.author_id in (?1) order by s.likes", nativeQuery = true)
    Page<Post> findFollowingPopularAllTime(List<User> users, Pageable page);

    @Query(value = "select * from posts as s where s.author_id in (?1) and s.creation_date > ?2 order by s.likes",
            nativeQuery = true)
    Page<Post> findFollowingPopularToday(List<User> users, LocalDateTime date, Pageable page);
}
