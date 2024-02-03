package ru.yandex.practicum.catsgram.feed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.feed.dto.FeedDto;
import ru.yandex.practicum.catsgram.feed.service.FeedService;

@RestController
@RequestMapping("/feed")
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('feed:get')")
    public FeedDto getFeed(@PathVariable Long userId,
                           @RequestParam(defaultValue = "0") int from,
                           @RequestParam(defaultValue = "10") int size) {
        return feedService.getFeed(userId, from, size);
    }
}
