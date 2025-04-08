package ru.yandex.practicum.filmorate.service;

import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.List;

public interface FilmService {
    List<FilmDto> findAll();

    FilmDto findById(int filmId);

    FilmDto create(Film film);

    FilmDto update(Film film);

    void remove(int filmId);

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    List<FilmDto> findPopularFilms(int count);

    List<Genre> findAllGenres();

    Genre findGenreById(int genreId);

    List<MPA> findAllMPA();

    MPA findMPAById(int mpaId);

    @Transactional(readOnly = true)
    List<FilmDto> getRecommendedFilms(int userId);
}
