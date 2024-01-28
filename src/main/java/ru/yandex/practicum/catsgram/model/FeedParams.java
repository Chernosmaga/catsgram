package ru.yandex.practicum.catsgram.model;

import lombok.Data;

import java.util.List;

@Data
public class FeedParams {
    private String sort;
    private Integer size;
    private List<Long> friends;
}
