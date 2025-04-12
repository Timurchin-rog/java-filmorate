package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Director> findAll() {
        return filmService.findAllDirectors();
    }

    @GetMapping("/{id}")
    public Director findById(@PathVariable int id) {
        return filmService.findDirectorById(id);
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        return filmService.createDirector(director);
    }

    @PutMapping()
    public Director update(@RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable int id) {
        filmService.removeDirector(id);
    }
}
