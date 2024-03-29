package ru.yandex.practicum.catsgram.comment.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.catsgram.comment.dto.CommentDto;
import ru.yandex.practicum.catsgram.comment.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentDto toCommentDto(Comment comment);
}
