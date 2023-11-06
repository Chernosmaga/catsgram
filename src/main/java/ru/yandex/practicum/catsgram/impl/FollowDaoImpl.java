package ru.yandex.practicum.catsgram.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.impl.dao.FollowDao;
import ru.yandex.practicum.catsgram.impl.dao.PostDao;
import ru.yandex.practicum.catsgram.impl.dao.UserDao;
import ru.yandex.practicum.catsgram.model.Follow;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FollowDaoImpl implements FollowDao {
    private final Logger log = LoggerFactory.getLogger(FollowDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;
    private final PostDao postDao;
    private final UserDao userDao;

    public FollowDaoImpl(JdbcTemplate jdbcTemplate, PostDao postDao, UserDao userDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.postDao = postDao;
        this.userDao = userDao;
    }

    @Override
    public List<Post> getFollowFeed(String userId, int max) {
        log.info("Запрос на получение постов автора {}", userId);
        // получаем все подписки пользователя
        String sql = "select * from cat_follow where follower_id = ?";
        List<Follow> follows = jdbcTemplate.query(sql, (rs, rowNum) -> makeFollow(rs), userId);

        // выгружаем авторов, на которых подписан пользователь
        Set<User> authors = follows.stream()
                .map(Follow::getUserId)
                .map(userDao::findUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        if (authors.isEmpty()) {
            return Collections.emptyList();
        }

        // выгружаем посты полученных выше авторов
        return authors.stream()
                .map(postDao::findPostsByUser)
                .flatMap(Collection::stream)
                // сортируем от новых к старым
                .sorted(Comparator.comparing(Post::getCreationDate).reversed())
                // отбрасываем лишнее
                .limit(max)
                .collect(Collectors.toList());
    }

    private Follow makeFollow(ResultSet rs) throws SQLException {
        // маппинг результата запроса в объект класса Follow
        return new Follow(rs.getString("author_id"), rs.getString("follower_id"));
    }
}
