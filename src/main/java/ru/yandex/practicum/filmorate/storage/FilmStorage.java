package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Map<Long, Film> getAllFilms();

    List<Film> findAllFilms();

    Film createFilm(Film film);

    Film updateFilm(long filmId, Film film);

    void removeFilm(long filmId);
}
