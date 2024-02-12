package ru.yandex.practicum.catsgram.subscription.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.subscription.service.SubscriptionService;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @PostMapping("/{followerId}")
    @ResponseStatus(CREATED)
    @PreAuthorize("hasAuthority('follow:create')")
    public void follow(@PathVariable Long followerId, @RequestParam Long userId) {
        subscriptionService.follow(followerId, userId);
    }

    @DeleteMapping("/{followerId}")
    @ResponseStatus(NO_CONTENT)
    @PreAuthorize("hasAuthority('follow:delete')")
    public void unfollow(@PathVariable Long followerId, @RequestParam Long userId) {
        subscriptionService.unfollow(followerId, userId);
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('follow:get')")
    public List<UserShortDto> getFollowers(@PathVariable Long userId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return subscriptionService.getFollowers(userId, from, size);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('follow:get')")
    public List<UserShortDto> getFollowing(@RequestParam Long userId,
                                           @RequestParam(defaultValue = "0") int from,
                                           @RequestParam(defaultValue = "10") int size) {
        return subscriptionService.getFollowing(userId, from, size);
    }

    @PatchMapping("/requests/{userId}")
    @PreAuthorize("hasAuthority('follow:create')")
    @ResponseStatus(NO_CONTENT)
    public void approveFollowing(@PathVariable Long userId,
                                 @RequestParam Long requesterId,
                                 @RequestParam Boolean isApproved) {
        subscriptionService.approveFollowing(userId, requesterId, isApproved);
    }

    @GetMapping("/requests/{userId}")
    @PreAuthorize("hasAuthority('follow:get')")
    public List<UserShortDto> getRequests(@PathVariable Long userId,
                                          @RequestParam(defaultValue = "0") int from,
                                          @RequestParam(defaultValue = "10") int size) {
        return subscriptionService.getRequesters(userId, from, size);
    }
}
