package ru.yandex.practicum.catsgram.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Service
public class HackCatService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String JDBC_URL="jdbc:postgresql://127.0.0.1:5432/cats";
    public static final String JDBC_USERNAME="kitty";
    public static final String JDBC_DRIVER="org.postgresql.Driver";

    public void tryPassword(String jdbcPassword) throws SQLException {
        DriverManagerDataSource dataSourceConst = new DriverManagerDataSource();
        dataSourceConst.setDriverClassName(JDBC_DRIVER);
        dataSourceConst.setUrl(JDBC_URL);
        dataSourceConst.setUsername(JDBC_USERNAME);
        dataSourceConst.setPassword(jdbcPassword);
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSourceConst);
        jdbcTemplate.execute("SELECT 1;");
    }

    public String doHackNow() {
        List<String> catWordList = Arrays.asList("meow", "purr", "purrrrrr", "zzz");
        try {
            for (String s : catWordList) {
                tryPassword(s);
                return s;
            }
        } catch (SQLException e) {
            log.info("Incorrect password");
        }
        return null;
    }
}