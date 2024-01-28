package ru.yandex.practicum.catsgram.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.AccessException;
import ru.yandex.practicum.catsgram.exception.IncorrectParameterException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.mapper.PostMapper;
import ru.yandex.practicum.catsgram.post.model.Post;
import ru.yandex.practicum.catsgram.post.repository.PostRepository;
import ru.yandex.practicum.catsgram.user.model.User;
import ru.yandex.practicum.catsgram.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final PostMapper postMapper;

    @Override
    public PostDto create(Long userId, PostDto post) {
        User user = findUser(userId);
        Post thisPost = new Post();
        if (post.getPhotoUrl() == null) {
            throw new IncorrectParameterException("Загрузите фото");
        }
        thisPost.setPhotoUrl(post.getPhotoUrl());
        thisPost.setAuthor(user);
        thisPost.setCreationDate(LocalDate.now());
        if (post.getDescription() != null) {
            thisPost.setDescription(post.getDescription());
        }
        return postMapper.toPostDto(postRepository.save(thisPost));
    }

    @Override
    public PostDto update(Long userId, Long postId, PostDto post) {
        User user = findUser(userId);
        Post thisPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
        if (thisPost.getAuthor() != user) {
            throw new AccessException("Нет доступа");
        }
        if (post.getDescription() != null) {
            thisPost.setDescription(post.getDescription());
        }
        if (post.getPhotoUrl() != null) {
            thisPost.setPhotoUrl(post.getPhotoUrl());
        }
        return postMapper.toPostDto(postRepository.save(thisPost));
    }

    @Override
    public PostDto getById(Long userId, Long postId) {
        Post thisPost = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Пост не найден"));
        return postMapper.toPostDto(thisPost);
    }

    @Override
    public void deleteById(Long userId, Long postId) {
        findUser(userId);
        postRepository.deleteById(postId);
    }

    @Override
    public List<PostDto> getOwnersPosts(Long ownerId, int page, int size) {
        User user = findUser(ownerId);
        PageRequest pages = PageRequest.of(page, size);
        return postRepository.findAllByAuthor(user, pages).stream().map(postMapper::toPostDto)
                .collect(Collectors.toList());
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }
}
