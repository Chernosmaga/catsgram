package ru.yandex.practicum.catsgram.post.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    Post toPost(PostDto post);
    PostDto toPostDto(Post post);
}
