package ru.yandex.practicum.catsgram.user.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.catsgram.user.dto.UserDto;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;
import ru.yandex.practicum.catsgram.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
