package ru.yandex.practicum.catsgram.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.impl.dao.PostDao;
import ru.yandex.practicum.catsgram.model.Post;
import ru.yandex.practicum.catsgram.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;

@Component
public class PostDaoImpl implements PostDao {
    private final Logger log = LoggerFactory.getLogger(PostDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public PostDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public Post create(Post post) {
        log.info("Добавление поста {}, пользователем {}", post, post.getAuthor());
        String input = "insert into cat_post (author_id, description, photo_url, creation_date) values (?, ?, ?, ?)";
        String output = "select author_id, description, photo_url, creation_date from cat_post where author_id=? " +
                " and description=? and photo_url=? and creation_date=?";
        jdbcTemplate.update(input, post);
        return jdbcTemplate.queryForObject(output, (rs, rowNum) -> makePost(post.getAuthor(), rs), post.getAuthor());
    }

    @Override
    public Collection<Post> findPostsByUser(User user) {
        log.info("Запрос на получение списка постов от пользователя {}", user);
        String sql = "select * from cat_post where author_id = ? order by creation_date desc";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makePost(user, rs), user.getId());
    }

    private Post makePost(User user, ResultSet rs) throws SQLException {
        // используем конструктор, методы ResultSet
        // и готовое значение user
        Integer id = rs.getInt("id");
        String description = rs.getString("description");
        String photoUrl = rs.getString("photo_url");
        // Получаем дату и конвертируем её из sql.Date в time.LocalDate
        LocalDate creationDate = rs.getDate("creation_date").toLocalDate();

        return new Post(id, user, description, photoUrl, creationDate);
    }
}
