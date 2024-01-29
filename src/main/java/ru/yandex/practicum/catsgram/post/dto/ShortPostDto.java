package ru.yandex.practicum.catsgram.post.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShortPostDto {
    private Long id;
    private String description;
    private String photoUrl;
}
