package ru.yandex.practicum.catsgram.post.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.comment.dto.CommentDto;
import ru.yandex.practicum.catsgram.comment.mapper.CommentMapper;
import ru.yandex.practicum.catsgram.comment.repository.CommentRepository;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.exception.IncorrectParameterException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.dto.ShortPostDto;
import ru.yandex.practicum.catsgram.post.mapper.PostMapper;
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
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    @Override
    public PostDto create(Long userId, ShortPostDto post) {
        User user = findUser(userId);
        Post thisPost = new Post();
        if (post.getPhotoUrl() == null) {
            throw new IncorrectParameterException("Загрузите фото");
        }
        thisPost.setPhotoUrl(post.getPhotoUrl());
        thisPost.setAuthor(user);
        thisPost.setCreationDate(LocalDateTime.now());
        if (post.getDescription() != null) {
            thisPost.setDescription(post.getDescription());
        }
        Post saved = postRepository.save(thisPost);
        log.info("Пользователь {} добавил пост: {}", user, saved);
        return postMapper.toPostDto(saved);
    }

    @Override
    public PostDto update(Long userId, Long postId, ShortPostDto post) {
        User user = findUser(userId);
        Post thisPost = findPost(postId);
        if (!thisPost.getAuthor().equals(user)) {
            throw new AccessException("Нет доступа");
        }
        if (post.getDescription() != null) {
            thisPost.setDescription(post.getDescription());
        }
        if (post.getPhotoUrl() != null) {
            thisPost.setPhotoUrl(post.getPhotoUrl());
        }
        log.info("Пользователь {} обновил пост: {}", user, post);
        return postMapper.toPostDto(postRepository.save(thisPost));
    }

    @Override
    public PostDto getById(Long userId, Long postId) {
        User user = findUser(userId);
        Post post = findPost(postId);
        PostDto thisPost = postMapper.toPostDto(post);
        thisPost.setComments(getComments(post));
        log.info("Пользователь {} запросил пост: {}", user, thisPost);
        return thisPost;
    }

    @Override
    public void deleteById(Long userId, Long postId) {
        User user = findUser(userId);
        Post post = findPost(postId);
        log.info("Пользователь {} удалил пост: {}", user, post);
        commentRepository.deleteAllByPost(post);
        postRepository.delete(post);
    }

    @Override
    public List<PostDto> getOwnersPosts(Long ownerId, int page, int size) {
        User user = findUser(ownerId);
        PageRequest pages = PageRequest.of(page, size);
        List<PostDto> posts = postRepository.findAllByAuthor(user, pages).stream()
                .peek(this::getComments).map(postMapper::toPostDto)
                .collect(Collectors.toList());
        log.info("Пользователь {} запросил список своих постов: {}", user, posts);
        return posts;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    private Post findPost(Long postId) {
        return postRepository.findById(postId).orElseThrow(() -> new NotFoundException("Пост не найден"));
    }

    private List<CommentDto> getComments(Post post) {
        List<CommentDto> comments = commentRepository.findAllByPost(post)
                .stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
        log.info("Возвращён список комментариев {} для поста {}", comments, post);
        return comments;
    }
}
