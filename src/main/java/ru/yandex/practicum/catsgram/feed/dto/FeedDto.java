package ru.yandex.practicum.catsgram.feed.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.post.dto.PostDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto {
    private LocalDateTime date;
    private List<PostDto> posts;
}
