package ru.yandex.practicum.catsgram.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.catsgram.enums.Role;
import ru.yandex.practicum.catsgram.enums.Status;

import javax.persistence.*;

import static javax.persistence.EnumType.STRING;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String username;
    private String nickname;
    private String password;
    @Enumerated(STRING)
    @CollectionTable(name = "role", joinColumns = @JoinColumn(name = "user_id"))
    private Role role;
    @Enumerated(STRING)
    @CollectionTable(name = "status", joinColumns = @JoinColumn(name = "user_id"))
    private Status status;
}