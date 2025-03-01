package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(long filmId, long userId) {
        if (filmStorage.getAllFilms().get(filmId) != null) {
            if (userStorage.getAllUsers().get(userId) != null) {
                Film film = filmStorage.getAllFilms().get(filmId);
                film.addLikedUser(userId);
                film.increaseCountLikes();
            } else {
                throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
            }
        } else {
            throw new NotFoundException(String.format("Фильм id = %d не найден", filmId));
        }
    }

    public void removeLike(long filmId, long userId) {
        if (filmStorage.getAllFilms().get(filmId) != null) {
            if (userStorage.getAllUsers().get(userId) != null) {
            Film film = filmStorage.getAllFilms().get(filmId);
            film.removeLikedUser(userId);
            film.reduceCountLikes();
            } else {
                throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
            }
        } else {
            throw new NotFoundException(String.format("Фильм id = %d не найден", filmId));
        }
    }

    public List<Film> findPopularFilms(long count) {
        if (filmStorage.getAllFilms().size() < count) {
            count = filmStorage.getAllFilms().size();
        }
        return filmStorage.getAllFilms().values().stream()
                .sorted(Comparator.comparing(Film::getCountLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }
}
