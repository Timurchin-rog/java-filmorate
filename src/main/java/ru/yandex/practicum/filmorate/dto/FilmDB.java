package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Date;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDB {
    Integer id;
    String name;
    String description;
    Date releaseDate;
    Integer duration;
    Set<Integer> likes;
    int countLikes;
    Set<Integer> genres;
    Integer mpa;
    Set<Integer> directors;
}
