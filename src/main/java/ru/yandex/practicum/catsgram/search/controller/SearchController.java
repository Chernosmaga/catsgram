package ru.yandex.practicum.catsgram.search.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.enums.PostSort;
import ru.yandex.practicum.catsgram.post.dto.PostDto;
import ru.yandex.practicum.catsgram.search.service.SearchService;
import ru.yandex.practicum.catsgram.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    @GetMapping("/posts/{userId}")
    public List<PostDto> findPosts(@PathVariable Long userId,
                                   @RequestParam @NotBlank String text,
                                   @RequestParam(defaultValue = "DATE") PostSort sort,
                                   @RequestParam(defaultValue = "0") int from,
                                   @RequestParam(defaultValue = "10") int size) {
        return searchService.findPosts(userId, text, sort, from, size);
    }

    @GetMapping("/users/{userId}")
    public List<UserShortDto> findUsers(@PathVariable Long userId,
                                        @RequestParam @NotBlank String text,
                                        @RequestParam(defaultValue = "0") int from,
                                        @RequestParam(defaultValue = "10") int size) {
        return searchService.findUsers(userId, text, from, size);
    }
}
