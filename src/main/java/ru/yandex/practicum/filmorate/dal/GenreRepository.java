package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_GENRES_OF_FILM = "SELECT g.id, g.name FROM genres AS g JOIN films_genres " +
            "AS fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
    private static final String FIND_GENRES_ID_OF_FILM = "SELECT g.id FROM genres AS g JOIN films_genres " +
            "AS fg ON g.id = fg.genre_id WHERE fg.film_id = ?";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM genres WHERE id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> getAllGenres() {
        List<Genre> genres = findMany("SELECT * FROM genres");
        return genres.stream()
                .sorted(Comparator.comparing(Genre::getId))
                .collect(Collectors.toList());
    }

    public Genre getGenreById(int genreId) {
        Optional<Genre> genreOpt = findOne(FIND_GENRE_BY_ID, genreId);
        if (genreOpt.isEmpty())
            throw new NotFoundException(String.format("Возрастной рейтинг id = %d не найден", genreId));
        return genreOpt.get();
    }

    public List<Integer> getGenresIdOfFilm(int filmId) {
        Set<Integer> genres = findManyId(FIND_GENRES_ID_OF_FILM, filmId);
        return new ArrayList<>(genres);
    }

    public List<Genre> getGenresOfFilm(int filmId) {
        return findMany(FIND_GENRES_OF_FILM, filmId);
    }

    public void addGenres(Integer filmId, Set<Integer> genresId) {
        for (Integer genreId : genresId) {
            insert(
                    "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)",
                    filmId,
                    genreId
            );
        }
    }

    public void checkGenres(Set<Integer> genresId) {
        for (Integer genreId : genresId) {
            Optional<Genre> genreOpt = findOne(FIND_GENRE_BY_ID, genreId);
            if (genreOpt.isEmpty())
                throw new NotFoundException(String.format("Жанр id = %d не найден", genreId));
        }
    }
}
