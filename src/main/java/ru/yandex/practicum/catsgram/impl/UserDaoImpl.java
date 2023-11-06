package ru.yandex.practicum.catsgram.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.catsgram.impl.dao.UserDao;
import ru.yandex.practicum.catsgram.model.User;

import java.util.Optional;

@Component
public class UserDaoImpl implements UserDao {
    private final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);
    private final JdbcTemplate jdbcTemplate;

    public UserDaoImpl(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public User create(User user) {
        log.info("Добавление нового пользователя: {}", user);
        jdbcTemplate.update("insert into cat_user (email, username, nickname) values (?, ?, ?)", user.getEmail(),
                user.getUsername(), user.getNickname());
        SqlRowSet userRow =
                jdbcTemplate.queryForRowSet("select * from cat_user where email=? and username=? and nickname=?",
                user.getEmail(), user.getUsername(), user.getNickname());
        return new User(userRow.getString("id"), userRow.getString("email"),
                userRow.getString("username"), userRow.getString("nickname"));
    }

    @Override
    public Optional<User> findUserById(String id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select * from cat_user where id = ?", id);
        if(userRows.next()) {
            log.info("Найден пользователь: {} {}", userRows.getString("id"),
                    userRows.getString("nickname"));
            User user = new User(
                    userRows.getString("id"),
                    userRows.getString("email"),
                    userRows.getString("username"),
                    userRows.getString("nickname"));
            user.setId(id);
            return Optional.of(user);
        } else {
            log.info("Пользователь с идентификатором {} не найден.", id);
            return Optional.empty();
        }
    }
}
