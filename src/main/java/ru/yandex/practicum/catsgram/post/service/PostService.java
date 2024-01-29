package ru.yandex.practicum.catsgram.post.service;

import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.dto.ShortPostDto;

import java.util.List;

public interface PostService {
    PostDto create(Long userId, ShortPostDto post);
    PostDto update(Long userId, Long postId, ShortPostDto post);
    PostDto getById(Long userId, Long postId);
    void deleteById(Long userId, Long postId);
    List<PostDto> getOwnersPosts(Long ownerId, int page, int size);
}
