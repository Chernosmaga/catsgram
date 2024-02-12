package ru.yandex.practicum.catsgram;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.catsgram.comment.dto.CommentDto;
import ru.yandex.practicum.catsgram.comment.repository.CommentRepository;
import ru.yandex.practicum.catsgram.comment.service.CommentService;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.exception.IncorrectParameterException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.dto.ShortPostDto;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.post.service.PostService;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static ru.yandex.practicum.catsgram.enums.Role.USER;
import static ru.yandex.practicum.catsgram.enums.Status.ACTIVE;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PostServiceImplTest {
    private final PostService postService;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
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
        commentRepository.deleteAll();
    }

    @Test
    void create_shouldCreatePost() {
        User saved = userRepository.save(user);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));


        assertThat(savedPost.getAuthor().getNickname(), equalTo(saved.getNickname()));
        assertNotNull(savedPost.getId());
        assertThat(savedPost.getPhotoUrl(), equalTo("https://link.com"));

    }

    @Test
    void create_shouldThrowExceptionIfPhotoUrlIsNull() {
        User saved = userRepository.save(user);

        assertThrows(IncorrectParameterException.class,
                () -> postService.create(saved.getId(), new ShortPostDto(null, "post",
                        null)));
    }

    @Test
    void update_shouldUpdateAllData() {
        User saved = userRepository.save(user);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));
        PostDto updatedPost = postService.update(saved.getId(), savedPost.getId(),
                new ShortPostDto(null, "new post", "https://newlink.com"));

        assertThat(updatedPost.getPhotoUrl(), equalTo("https://newlink.com"));
        assertThat(updatedPost.getDescription(), equalTo("new post"));
        assertThat(updatedPost.getAuthor().getNickname(), equalTo(saved.getNickname()));
    }

    @Test
    void update_shouldThrowExceptionIfPostIsNotFound() {
        User saved = userRepository.save(user);

        assertThrows(NotFoundException.class,
                () -> postService.update(saved.getId(), 999L,
                        new ShortPostDto(null, "new post", "https://newlink.com")));
    }

    @Test
    void getById_shouldReturnPostById() {
        User saved = userRepository.save(user);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));
        PostDto returned = postService.getById(saved.getId(), savedPost.getId());

        assertThat(returned.getAuthor(), equalTo(savedPost.getAuthor()));
        assertThat(returned.getDescription(), equalTo(savedPost.getDescription()));
        assertThat(returned.getPhotoUrl(), equalTo(savedPost.getPhotoUrl()));
    }

    @Test
    void getById_shouldThrowExceptionIfIdIsIncorrect() {
        User saved = userRepository.save(user);
        assertThrows(NotFoundException.class,
                () -> postService.getById(saved.getId(), 999L));
    }

    @Test
    void deleteById_shouldDeletePostById() {
        User saved = userRepository.save(user);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));
        postService.deleteById(saved.getId(), savedPost.getId());

        assertThrows(NotFoundException.class,
                () -> postService.getById(saved.getId(), savedPost.getId()));
    }

    @Test
    void deleteById_shouldThrowExceptionIfPostNotFound() {
        User saved = userRepository.save(user);

        assertThrows(NotFoundException.class,
                () -> postService.deleteById(saved.getId(), 999L));
    }

    @Test
    void getOwnersPosts_shouldReturnPostsForOwner() {
        User saved = userRepository.save(user);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));
        PostDto secondSaved = postService.create(saved.getId(), new ShortPostDto(null, "second post",
                "https://secondlink.com"));
        List<PostDto> posts = postService.getOwnersPosts(saved.getId(), 0, 10);

        assertNotNull(posts);
        assertEquals(2, posts.size());
        assertTrue(posts.contains(savedPost));
        assertTrue(posts.contains(secondSaved));
    }

    @Test
    void addComment_shouldAddComment() {
        User saved = userRepository.save(user);
        User secondSaved = userRepository.save(secondUser);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));
        CommentDto comment = commentService.addComment(secondSaved.getId(), savedPost.getId(), "comment");

        assertNotNull(comment.getId());
        assertEquals(comment.getAuthor().getNickname(), secondSaved.getNickname());
    }

    @Test
    void deleteComment_shouldDeleteCommentByOwnerOfThePost() {
        User saved = userRepository.save(user);
        User secondSaved = userRepository.save(secondUser);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));
        CommentDto comment = commentService.addComment(secondSaved.getId(), savedPost.getId(), "comment");
        commentService.deleteComment(saved.getId(), savedPost.getId(), comment.getId());

        assertTrue(commentService.getComments(secondSaved.getId(), 0, 10).isEmpty());
    }

    @Test
    void deleteComment_shouldDeleteCommentByPersonWhoPosted() {
        User saved = userRepository.save(user);
        User secondSaved = userRepository.save(secondUser);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));
        CommentDto comment = commentService.addComment(secondSaved.getId(), savedPost.getId(), "comment");
        commentService.deleteComment(secondSaved.getId(), savedPost.getId(), comment.getId());

        assertTrue(commentService.getComments(secondSaved.getId(), 0, 10).isEmpty());
    }

    @Test
    void deleteComment_shouldThrowExceptionIfUserNotFound() {
        assertThrows(NotFoundException.class,
                () -> commentService.deleteComment(999L, 999L, 999L));
    }

    @Test
    void deleteComment_shouldThrowExceptionIfPostNotFound() {
        User saved = userRepository.save(user);

        assertThrows(NotFoundException.class,
                () -> commentService.deleteComment(saved.getId(), 999L, 999L));
    }

    @Test
    void deleteComment_shouldThrowExceptionIfNotOwnerNeitherPersonWhoPostedDeletingComment() {
        User saved = userRepository.save(user);
        User secondSaved = userRepository.save(secondUser);
        User thirdSaved = userRepository.save(thirdUser);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));
        CommentDto comment = commentService.addComment(secondSaved.getId(), savedPost.getId(), "comment");

        assertThrows(AccessException.class,
                () -> commentService.deleteComment(thirdSaved.getId(), savedPost.getId(), comment.getId()));
    }

    @Test
    void getComments_shouldReturnListOfComments() {
        User saved = userRepository.save(user);
        User secondSaved = userRepository.save(secondUser);
        PostDto savedPost = postService.create(saved.getId(), new ShortPostDto(null, "post",
                "https://link.com"));
        commentService.addComment(secondSaved.getId(), savedPost.getId(),
                "first comment");
        commentService.addComment(secondSaved.getId(), savedPost.getId(),
                "second comment");
        List<CommentDto> comments = commentService.getComments(secondSaved.getId(), 0, 10);

        assertFalse(comments.isEmpty());
        assertEquals(2, comments.size());
    }

    @Test
    void getComments_shouldThrowExceptionIfUserIdIsIncorrect() {
        assertThrows(NotFoundException.class,
                () -> commentService.getComments(999L, 0, 10));
    }
}
