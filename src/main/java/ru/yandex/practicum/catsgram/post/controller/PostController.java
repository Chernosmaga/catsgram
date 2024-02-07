package ru.yandex.practicum.catsgram.post.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.dto.ShortPostDto;
import ru.yandex.practicum.catsgram.post.service.PostService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/posts/{userId}")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    @PreAuthorize("hasAuthority('post:get')")
    public List<PostDto> findAll(@PathVariable Long userId,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size) {
        return postService.getOwnersPosts(userId, page, size);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('post:create')")
    @ResponseStatus(CREATED)
    public PostDto create(@PathVariable Long userId, @RequestParam ShortPostDto post) {
        return postService.create(userId, post);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:delete')")
    @ResponseStatus(NO_CONTENT)
    public void delete(@PathVariable Long userId, @PathVariable Long postId) {
        postService.deleteById(userId, postId);
    }

    @PatchMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:update')")
    public PostDto update(@PathVariable Long userId, @PathVariable Long postId,
                          @RequestParam ShortPostDto post) {
        return postService.update(userId, postId, post);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:get')")
    public PostDto getById(@PathVariable Long userId, @PathVariable Long postId) {
        return postService.getById(userId, postId);
    }
}
