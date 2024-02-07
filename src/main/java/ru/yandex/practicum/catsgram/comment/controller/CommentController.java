package ru.yandex.practicum.catsgram.comment.controller;

import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.comment.dto.CommentDto;
import ru.yandex.practicum.catsgram.comment.service.CommentService;

import javax.validation.constraints.NotBlank;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping("/comments/{userId}/{postId}")
    @PreAuthorize("hasAuthority('comment:create')")
    @ResponseStatus(CREATED)
    public CommentDto createComment(@PathVariable Long userId, @PathVariable Long postId,
                                    @RequestBody @NotBlank @Length(min = 5, max = 200) String text) {
        return commentService.addComment(userId, postId, text);
    }

    @DeleteMapping("/comments/{userId}/{postId}/{commentId}")
    @PreAuthorize("hasAuthority('comment:delete')")
    @ResponseStatus(NO_CONTENT)
    public void deleteComment(@PathVariable Long userId, @PathVariable Long postId,
                              @PathVariable Long commentId) {
        commentService.deleteComment(userId, postId, commentId);
    }

    @GetMapping("/comments/{userId}")
    @PreAuthorize("hasAuthority('comment:get')")
    public List<CommentDto> getComments(@PathVariable Long userId,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        return commentService.getComments(userId, from, size);
    }
}
