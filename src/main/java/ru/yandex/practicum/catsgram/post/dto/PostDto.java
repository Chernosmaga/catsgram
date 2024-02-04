package ru.yandex.practicum.catsgram.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.comment.dto.CommentDto;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDto {
    private Long id;
    private UserShortDto author;
    private String description;
    private String photoUrl;
    private Long likes;
    private List<CommentDto> comments;
}
