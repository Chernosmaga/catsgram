package ru.yandex.practicum.catsgram.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Post implements Comparable<Post> {
    private Integer id;
    private User author; // автор
    private String description; // описание
    private String photoUrl; // url-адрес фотографии
    private LocalDate creationDate; // дата создания

    @Override
    public int compareTo(Post o) {
        return this.getCreationDate().compareTo(o.getCreationDate());
    }
}
