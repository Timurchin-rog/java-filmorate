package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;
    private final String pathLike = "/{filmId}/like/{userId}";

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable long filmId) {
        filmStorage.removeFilm(filmId);
    }

    @PutMapping(pathLike)
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(pathLike)
    public void removeLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") long count) {
        return filmService.findPopularFilms(count);
    }
}