package ru.yandex.practicum.catsgram.controller.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class RegRequestDto {
    @Email
    @NotBlank
    private String email;
    @NotBlank
    @Length(min = 6, max = 20)
    private String username;
    @NotBlank
    @Length(min = 5, max = 10)
    private String nickname;
    @NotBlank
    @Length(min = 8, max = 12)
    private String password;
}
