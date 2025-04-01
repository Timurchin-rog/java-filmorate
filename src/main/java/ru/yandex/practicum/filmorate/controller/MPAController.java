package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MPAController {
    private final FilmService filmService;

    @GetMapping
    public Collection<MPA> findAllMPA() {
        return filmService.findAllMPA();
    }

    @GetMapping("/{mpaId}")
    public MPA findMPAById(@PathVariable int mpaId) {
        return filmService.findMPAById(mpaId);
    }
}
