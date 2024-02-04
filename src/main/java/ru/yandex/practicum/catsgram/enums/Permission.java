package ru.yandex.practicum.catsgram.enums;

import lombok.Getter;

@Getter
public enum Permission {
    USER_CREATE("user:create"),
    USER_DELETE("user:delete"),
    USER_UPDATE("user:update"),
    USER_GET("user:get"),
    POST_CREATE("post:create"),
    POST_DELETE("post:delete"),
    POST_UPDATE("post:update"),
    POST_GET("post:get"),
    COMMENT_CREATE("comment:create"),
    COMMENT_DELETE("comment:delete"),
    COMMENT_GET("comment:get"),
    FOLLOW("follow:create"),
    UNFOLLOW("follow:delete"),
    FOLLOW_GET("follow:get"),
    FEED_GET("feed:get"),
    LIKE_POST("like:create"),
    LIKE_DELETE("like:delete"),
    LIKE_GET("like:get"),
    FOLLOWING_POPULAR_GET("following_popular:get"),
    WORLDWIDE_POPULAR_GET("worldwide_popular:get");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

}
