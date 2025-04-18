package ru.yandex.practicum.filmorate.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class DirectorRepository extends BaseRepository<Director> {
    private static final String FIND_DIRECTOR_BY_ID = "SELECT * FROM directors WHERE id = ?";
    private static final String FIND_DIRECTORS_ID_OF_FILM = "SELECT d.id FROM directors AS d JOIN films_directors " +
            "AS fd ON d.id = fd.director_id WHERE fd.film_id = ?";
    private static final String FIND_DIRECTORS_OF_FILM = "SELECT d.id, d.name FROM directors AS d JOIN films_directors " +
            "AS fd ON d.id = fd.director_id WHERE fd.film_id = ?";
    private static final String UPDATE_DIRECTOR = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_DIRECTOR = "DELETE FROM directors WHERE id = ?";
    private static final String DELETE_DIRECTOR_FROM_FILMS_LIST = "DELETE FROM films_directors WHERE director_id = ?";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    public List<Director> getAllDirectors() {
        List<Director> directors = findMany("SELECT * FROM directors");
        return directors.stream()
                .sorted(Comparator.comparing(Director::getId))
                .collect(Collectors.toList());
    }

    public Director getDirectorById(int directorId) {
        Optional<Director> directorOpt = findOne(FIND_DIRECTOR_BY_ID, directorId);
        if (directorOpt.isEmpty())
            throw new NotFoundException();
        return directorOpt.get();
    }

    public Set<Integer> getDirectorsIdOfFilm(int filmId) {
        return findManyId(FIND_DIRECTORS_ID_OF_FILM, filmId);
    }

    public Set<Director> getDirectorsOfFilm(int filmId) {
        return new HashSet<>(findMany(FIND_DIRECTORS_OF_FILM, filmId));
    }

    public void addDirectors(int filmId, Set<Integer> directorsId) {
        if (directorsId.isEmpty())
            return;
        for (Integer directorId : directorsId) {
            insert(
                    "INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)",
                    filmId,
                    directorId
            );
        }
    }

    public void saveDirector(Director director) {
        int id = insert(
                "INSERT INTO directors (name) VALUES (?)",
                director.getName()
        );
        director.setId(id);
    }

    public void deleteDirectorsInFilm(Integer filmId) {
        jdbc.update("DELETE FROM films_directors WHERE film_id = ?", filmId);
    }

    public void removeDirector(int directorId) {
        delete(DELETE_DIRECTOR_FROM_FILMS_LIST, directorId);
        delete(DELETE_DIRECTOR, directorId);
    }

    public void updateDirector(Director director) {
        update(
                UPDATE_DIRECTOR,
                director.getName(),
                director.getId()
        );
    }

    public void checkDirectors(Set<Integer> directorsId) {
        for (Integer directorId : directorsId) {
            Optional<Director> directorOpt = findOne(FIND_DIRECTOR_BY_ID, directorId);
            if (directorOpt.isEmpty())
                throw new NotFoundException();
        }
    }
}