package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer id;
    String name;
    String description;
    Date releaseDate;
    Integer duration;
    Set<Integer> likes;
    int countLikes;
    List<Genre> genres;
    MPA mpa;
    List<Director> directors;
}