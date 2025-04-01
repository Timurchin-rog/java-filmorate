package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDB {
    Integer id;
    String name;
    String description;
    Date release_date;
    Integer duration;
    Set<Integer> likes;
    int count_likes;
    List<Integer> genres;
    Integer mpa;
}
