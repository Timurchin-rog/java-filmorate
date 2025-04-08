package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.sql.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Film {
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

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasDescription() {
        return ! (description == null || description.isBlank());
    }

    public boolean hasReleaseDate() {
        return ! (releaseDate == null);
    }

    public boolean hasDuration() {
        return ! (duration == null);
    }

    public boolean hasGenres() {
        return ! (genres == null);
    }

    public boolean hasMpa() {
        return ! (mpa == null);
    }

    public boolean hasDirectors() {
        return ! (directors == null);
    }

    public static class FilmBuilder {

        public Film.FilmBuilder name(String name) {
            if (name == null || name.isBlank()) {
                throw new ValidationException("Название фильма не может быть пустым");
            }
            this.name = name;
            return this;
        }

        public Film.FilmBuilder description(String description) {
            if (description == null || description.isBlank()) {
                throw new ValidationException("Описание фильма не может быть пустым");
            }
            if (description.length() > 200) {
                throw new ValidationException("Максимальная длина описания — 200 символов");
            }
            this.description = description;
            return this;
        }

        public Film.FilmBuilder releaseDate(Date releaseDate) {
            if (releaseDate == null) {
                throw new ValidationException("Дата релиза не должна быть пустой");
            }
            String cinemaBirthdayStr = "1895-12-28";
            if (releaseDate.compareTo(Date.valueOf(cinemaBirthdayStr)) < 0) {
                throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
            }
            this.releaseDate = releaseDate;
            return this;
        }

        public Film.FilmBuilder duration(Integer duration) {
            if (duration == null) {
                throw new ValidationException("Продолжительность фильма не должна быть пустой");
            }
            if (duration < 0) {
                throw new ValidationException("Продолжительность фильма должна быть положительной");
            }
            this.duration = duration;
            return this;
        }

        public Film.FilmBuilder countLikes(int countLikes) {
            this.countLikes += countLikes;
            return this;
        }
    }
}
