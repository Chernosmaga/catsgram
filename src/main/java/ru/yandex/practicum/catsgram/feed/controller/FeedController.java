package ru.yandex.practicum.catsgram.feed.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.enums.DateSort;
import ru.yandex.practicum.catsgram.feed.service.FeedService;
import ru.yandex.practicum.catsgram.post.dto.PostDto;

import java.util.List;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class FeedController {
    private final FeedService feedService;

    @GetMapping("/feed/{userId}")
    @PreAuthorize("hasAuthority('feed:get')")
    public List<PostDto> getFeed(@PathVariable Long userId,
                                 @RequestParam(defaultValue = "0") int from,
                                 @RequestParam(defaultValue = "10") int size) {
        return feedService.getFeed(userId, from, size);
    }

    @GetMapping("/popular/following/{userId}")
    @PreAuthorize("hasAuthority('following_popular:get')")
    public List<PostDto> getFollowingPopular(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "TODAY") DateSort sort,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return feedService.getFollowingPopular(userId, sort, from, size);
    }

    @GetMapping("/popular/worldwide/{userId}")
    @PreAuthorize("hasAuthority('worldwide_popular:get')")
    public List<PostDto> getWorldwidePopular(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "TODAY") DateSort sort,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return feedService.getWorldwidePopular(userId, sort, from, size);
    }
}
