package ru.yandex.practicum.catsgram.comment.service;

import ru.yandex.practicum.catsgram.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long postId, String text);

    void deleteComment(Long userId, Long postId, Long commentId);

    List<CommentDto> getComments(Long userId, int from, int size);
}
