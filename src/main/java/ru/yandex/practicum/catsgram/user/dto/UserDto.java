package ru.yandex.practicum.catsgram.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    @Email
    private String email;
    @NotBlank
    private String username;
    private String nickname;
    private Boolean isClosed;
}
