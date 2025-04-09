package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.sql.Date;
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
    Set<Genre> genres;
    MPA mpa;
    Set<Director> directors;

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
                throw new ValidationException();
            }
            this.name = name;
            return this;
        }

        public Film.FilmBuilder description(String description) {
            if (description == null || description.isBlank()) {
                throw new ValidationException();
            }
            if (description.length() > 200) {
                throw new ValidationException();
            }
            this.description = description;
            return this;
        }

        public Film.FilmBuilder releaseDate(Date releaseDate) {
            if (releaseDate == null) {
                throw new ValidationException();
            }
            String cinemaBirthdayStr = "1895-12-28";
            if (releaseDate.compareTo(Date.valueOf(cinemaBirthdayStr)) < 0) {
                throw new ValidationException();
            }
            this.releaseDate = releaseDate;
            return this;
        }

        public Film.FilmBuilder duration(Integer duration) {
            if (duration == null) {
                throw new ValidationException();
            }
            if (duration < 0) {
                throw new ValidationException();
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
