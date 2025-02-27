package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public Map<Long, Film> getAllFilms() {
        return films;
    }

    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    public Film createFilm(Film film) {
        checkValidation(film);
        film.setId(getNextId());
        log.info(String.format("Валидация нового фильма id = %d пройдена", film.getId()));
        films.put(film.getId(), film);
        log.info(String.format("Новый фильм id = %d добавлен", film.getId()));
        return film;
    }

    public Film updateFilm(long filmId, Film newFilm) {
        if (films.get(filmId) != null) {
            checkValidation(newFilm);
            log.info(String.format("Валидация обновлённого фильма id = %d пройдена", filmId));
            newFilm.setId(filmId);
            Film oldFilm = films.get(filmId);
            newFilm.setLikes(oldFilm.getLikes());
            newFilm.setCountLikes(oldFilm.getCountLikes());
            films.put(newFilm.getId(), newFilm);
            log.info(String.format("Фильм id = %d обновлён", filmId));
            return newFilm;
        } else {
            throw new NotFoundException(String.format("Фильм id = %d не найден", filmId));
        }
    }

    public void removeFilm(long filmId) {
        films.remove(filmId);
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
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            throw new ValidationException("Описание фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза не должна быть пустой");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза должна быть не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null) {
            throw new ValidationException("Продолжительность фильма не должна быть пустой");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
