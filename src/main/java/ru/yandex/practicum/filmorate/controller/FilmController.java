package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final String pathLike = "/{film-id}/like/{user-id}";

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{film-id}")
    public FilmDto findById(@PathVariable(name = "film-id") int filmId) {
        return filmService.findById(filmId);
    }

    @PostMapping
    public FilmDto create(@RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping()
    public FilmDto update(@RequestBody Film film) {
        return filmService.update(film);
    }

    @DeleteMapping("/{film-id}")
    public void remove(@PathVariable(name = "film-id") int filmId) {
        filmService.remove(filmId);
    }

    @PutMapping(pathLike)
    public void addLike(@PathVariable(name = "film-id") int filmId,
                        @PathVariable(name = "user-id") int userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping(pathLike)
    public void removeLike(@PathVariable(name = "film-id") int filmId,
                           @PathVariable(name = "user-id") int userId) {
        filmService.removeLike(filmId, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> findPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.findPopularFilms(count);
    }

    @GetMapping("/search")
    public List<FilmDto> searchFilms(@RequestParam String query,
                                     @RequestParam String by) {
        return filmService.searchFilms(query, by);
    }

    @GetMapping("/common")
    public Collection<FilmDto> getCommonFilms(@RequestParam int userId,
                                              @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }
  
    @GetMapping("/director/{id}")
    public List<FilmDto> findPopularFilms(@PathVariable int id, @RequestParam("year") String sortBy) {
        return filmService.findFilmsOfDirector(id, sortBy);
    }

    @GetMapping("/recommendations")
    public List<FilmDto> getRecommendations(@RequestParam(name = "user-id") int userId) {
        return filmService.getRecommendedFilms(userId);
    }

    @GetMapping("/user/{userId}")
    public List<FilmDto> getFilmsByUser(@PathVariable int userId) {
        return filmService.getFilmsByUserId(userId);
    }
}

