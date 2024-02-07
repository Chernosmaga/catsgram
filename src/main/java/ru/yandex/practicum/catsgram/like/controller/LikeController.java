package ru.yandex.practicum.catsgram.like.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.like.service.LikeService;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/likes/{userId}")
    @PreAuthorize("hasAuthority('like:create')")
    @ResponseStatus(CREATED)
    public void like(@PathVariable Long userId, @RequestParam Long postId) {
        likeService.like(userId, postId);
    }

    @DeleteMapping("/likes/{userId}")
    @PreAuthorize("hasAuthority('like:delete')")
    @ResponseStatus(NO_CONTENT)
    public void dislike(@PathVariable Long userId, @RequestParam Long postId) {
        likeService.dislike(userId, postId);
    }

    @GetMapping("/likes/{userId}")
    @PreAuthorize("hasAuthority('like:get')")
    public List<UserShortDto> getLikedUsers(@PathVariable Long userId,
                                            @RequestParam Long postId,
                                            @RequestParam(defaultValue = "0") int from,
                                            @RequestParam(defaultValue = "10") int size) {
        return likeService.getLikedUsers(userId, postId, from, size);
    }
}
