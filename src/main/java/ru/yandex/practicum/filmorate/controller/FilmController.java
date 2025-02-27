package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> findAllFilms() {
        return filmService.getFilmStorage().findAllFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        return filmService.getFilmStorage().createFilm(film);
    }

    @PutMapping("/{filmId}")
    public Film updateFilm(@PathVariable long filmId, @RequestBody Film newFilm) {
        return filmService.getFilmStorage().updateFilm(filmId, newFilm);
    }

    @DeleteMapping("/{filmId}")
    public void removeFilm(@PathVariable long filmId) {
        filmService.getFilmStorage().removeFilm(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void removeLike(@PathVariable long filmId, @PathVariable long userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") long count) {
        return filmService.findPopularFilms(count);
    }
}