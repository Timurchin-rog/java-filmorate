package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.FilmDB;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<FilmDB> {
    @Override
    public FilmDB mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return FilmDB.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date"))
                .duration(resultSet.getInt("duration"))
                .countLikes(resultSet.getInt("count_likes"))
                .mpa(resultSet.getInt("mpa"))
                .build();
    }
}
