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
    private final String HEADER = "User-Catsgram";

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('user:get')")
    public UserShortDto getById(@PathVariable Long userId){
        return userService.getById(userId);
    }

    @PutMapping
    @PreAuthorize("hasAuthority('user:update')")
    public UserShortDto update(@RequestHeader(HEADER) Long userId, @RequestBody UserDto user) {
        return userService.update(userId, user);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('user:delete')")
    public void delete(@RequestHeader(HEADER) Long userId) {
        userService.deleteById(userId);
    }

    @GetMapping("/owner")
    @PreAuthorize("hasAuthority('user:get')")
    public UserDto getByOwner(@RequestHeader(HEADER) Long userId) {
        return userService.getAccount(userId);
    }
}
