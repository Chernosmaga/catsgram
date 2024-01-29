package ru.yandex.practicum.catsgram.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.comment.dto.CommentDto;
import ru.yandex.practicum.catsgram.comment.mapper.CommentMapper;
import ru.yandex.practicum.catsgram.comment.model.Comment;
import ru.yandex.practicum.catsgram.comment.repository.CommentRepository;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentMapper commentMapper;

    @Override
    public CommentDto addComment(Long userId, Long postId, String text) {
        User user = findUser(userId);
        Post post = findPost(postId);
        Comment comment = new Comment();
        comment.setText(text);
        comment.setAuthor(user);
        comment.setCreationDate(LocalDateTime.now());
        comment.setPost(post);
        Comment saved = commentRepository.save(comment);
        log.info("Добавлен комментарий {} к посту {} от пользователя {}", text, post, user);
        return commentMapper.toCommentDto(saved);
    }

    @Override
    public void deleteComment(Long userId, Long postId, Long commentId) {
        User user = findUser(userId);
        Post post = findPost(postId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий не найден"));
        if (!comment.getAuthor().equals(user) && !post.getAuthor().equals(user)) {
            throw new AccessException("Нет доступа");
        }
        log.info("Удален комментарий {} к посту {} пользователем {}", comment.getText(), post, user);
        commentRepository.delete(comment);
    }

    @Override
    public List<CommentDto> getComments(Long userId, int from, int size) {
        PageRequest pageRequest = PageRequest.of(from, size);
        User user = findUser(userId);
        List<CommentDto> comments = commentRepository.findAllByAuthor(user, pageRequest)
                .stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
        log.info("Возвращёны комментарии по запросу пользователя: {}", comments);
        return comments;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
    }
}
