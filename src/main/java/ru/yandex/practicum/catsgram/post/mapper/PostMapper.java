package ru.yandex.practicum.catsgram.post.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.model.Post;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @Mapping(target = "tags", ignore = true)
    PostDto toPostDto(Post post);
}
