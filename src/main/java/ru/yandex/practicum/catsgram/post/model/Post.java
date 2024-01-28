package ru.yandex.practicum.catsgram.post.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "posts")
@AllArgsConstructor
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "users")
    private User author;
    private String description;
    @Column(name = "photo_url")
    private String photoUrl;
    @Column(name = "creation_date")
    private LocalDate creationDate;
}
