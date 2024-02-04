package ru.yandex.practicum.catsgram.post.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.comment.dto.CommentDto;
import ru.yandex.practicum.catsgram.comment.service.CommentService;
import ru.yandex.practicum.catsgram.like.service.LikeService;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.post.dto.ShortPostDto;
import ru.yandex.practicum.catsgram.post.service.PostService;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
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
    @ResponseStatus(CREATED)
    public PostDto create(@RequestHeader(HEADER) Long userId, @RequestParam ShortPostDto post) {
        return postService.create(userId, post);
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:delete')")
    @ResponseStatus(NO_CONTENT)
    public void delete(@RequestHeader(HEADER) Long userId, @PathVariable Long postId) {
        postService.deleteById(userId, postId);
    }

    @PatchMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:update')")
    public PostDto update(@RequestHeader(HEADER) Long userId, @PathVariable Long postId,
                          @RequestParam ShortPostDto post) {
        return postService.update(userId, postId, post);
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasAuthority('post:get')")
    public PostDto getById(@RequestHeader(HEADER) Long userId, @PathVariable Long postId) {
        return postService.getById(userId, postId);
    }

    @PostMapping("/comments/{postId}")
    @PreAuthorize("hasAuthority('comment:create')")
    @ResponseStatus(CREATED)
    public CommentDto createComment(@RequestHeader(HEADER) Long userId, @PathVariable Long postId,
                                    @RequestBody @NotBlank @Length(min = 5, max = 200) String text) {
        return commentService.addComment(userId, postId, text);
    }

    @DeleteMapping("/comments/{postId}/{commentId}")
    @PreAuthorize("hasAuthority('comment:delete')")
    @ResponseStatus(NO_CONTENT)
    public void deleteComment(@RequestHeader(HEADER) Long userId, @PathVariable Long postId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, postId, commentId);
    }

    @GetMapping("/comments")
    @PreAuthorize("hasAuthority('comment:get')")
    public List<CommentDto> getComments(@RequestHeader(HEADER) Long userId,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        return commentService.getComments(userId, from, size);
    }

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
