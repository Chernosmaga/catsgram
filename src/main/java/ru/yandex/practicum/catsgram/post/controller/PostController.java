package ru.yandex.practicum.catsgram.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.service.PostService;

import java.util.List;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final String HEADER = "User-Catsgram";

    @GetMapping
    @PreAuthorize("hasAuthority('post:get')")
    public List<PostDto> findAll(@RequestHeader(HEADER) Long userId,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size) {
        return postService.getOwnersPosts(userId, page, size);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('post:create')")
    public PostDto create(@RequestHeader(HEADER) Long userId, @RequestParam PostDto post) {
        return postService.create(userId, post);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:delete')")
    public void delete(@RequestHeader(HEADER) Long userId, @PathVariable Long postId) {
        postService.deleteById(userId, postId);
    }

    @PatchMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:update')")
    public PostDto update(@RequestHeader(HEADER) Long userId, @PathVariable Long postId, @RequestParam PostDto post) {
        return postService.update(userId, postId, post);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:get')")
    public PostDto getById(@RequestHeader(HEADER) Long userId, @PathVariable Long postId) {
        return postService.getById(userId, postId);
    }
}
