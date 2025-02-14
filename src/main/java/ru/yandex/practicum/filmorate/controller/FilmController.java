package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EmptyIdException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    @Getter
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        checkValidation(film);
        film.setId(getNextId());
        log.info(String.format("Валидация нового фильма id = %d пройдена", film.getId()));
        films.put(film.getId(), film);
        log.info(String.format("Новый фильм id = %d добавлен", film.getId()));
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (film.getId() == null) {
            throw new EmptyIdException("Id должен быть указан");
        }
        if (films.get(film.getId()) != null) {
            checkValidation(film);
            log.info(String.format("Валидация обновлённого фильма id = %d пройдена", film.getId()));
            films.put(film.getId(), film);
            log.info(String.format("Фильм id = %d обновлён", film.getId()));
            return film;
        } else {
            throw new NotFoundException(String.format("Фильм id = %d не найден", film.getId()));
        }
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkValidation(Film film) {
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
    }
}