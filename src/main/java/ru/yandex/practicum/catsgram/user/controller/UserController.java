package ru.yandex.practicum.catsgram.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.user.dto.UserDto;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;
import ru.yandex.practicum.catsgram.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/{requesterId}")
    @PreAuthorize("hasAuthority('user:get')")
    public UserShortDto getById(@PathVariable Long requesterId,
                                @RequestParam Long userId){
        return userService.getById(requesterId, userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:update')")
    public UserShortDto update(@PathVariable Long userId, @RequestBody UserDto user) {
        return userService.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:delete')")
    public void delete(@PathVariable Long userId) {
        userService.deleteById(userId);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:get')")
    public UserDto getByOwner(@PathVariable Long userId) {
        return userService.getAccount(userId);
    }
}
