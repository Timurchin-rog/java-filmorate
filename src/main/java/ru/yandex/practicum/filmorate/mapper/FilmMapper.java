package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDB;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class FilmMapper {
    public static FilmDB mapToFilmDB(Film film) {
        FilmDB filmDB = FilmDB.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .countLikes(film.getCountLikes())
                .build();
        if (film.getMpa() != null)
            filmDB.setMpa(film.getMpa().getId());
        if (film.getGenres() != null) {
            Set<Integer> genresId = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            filmDB.setGenres(genresId);
        }
        if (film.getDirectors() != null) {
            Set<Integer> directorsId = film.getDirectors().stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());
            filmDB.setDirectors(directorsId);
        }
        return filmDB;
    }

    public static FilmDto mapToFilmDto(Film film) {
        return FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration())
                .likes(film.getLikes())
                .countLikes(film.getCountLikes())
                .genres(film.getGenres().stream()
                        .sorted(Comparator.comparing(Genre::getId))
                        .toList())
                .mpa(film.getMpa())
                .directors(film.getDirectors().stream()
                        .sorted(Comparator.comparing(Director::getId))
                        .toList())
                .build();
    }

    public static FilmDB updateFilmFields(FilmDB filmDB, Film filmFromRequest) {
        if (filmFromRequest.hasName()) {
            filmDB.setName(filmFromRequest.getName());
        }
        if (filmFromRequest.hasDescription()) {
            filmDB.setDescription(filmFromRequest.getDescription());
        }
        if (filmFromRequest.hasReleaseDate()) {
            filmDB.setReleaseDate(filmFromRequest.getReleaseDate());
        }
        if (filmFromRequest.hasDuration()) {
            filmDB.setDuration(filmFromRequest.getDuration());
        }
        if (filmFromRequest.hasGenres() && !filmFromRequest.getGenres().isEmpty()) {
            Set<Integer> genresId = filmFromRequest.getGenres().stream()
                            .map(Genre::getId)
                            .collect(Collectors.toSet());
            filmDB.setGenres(genresId);
        } else {
            filmDB.setGenres(new HashSet<>());
        }
        if (filmFromRequest.hasMpa()) {
            filmDB.setMpa(filmFromRequest.getMpa().getId());
        }
        if (filmFromRequest.hasDirectors() && !filmFromRequest.getDirectors().isEmpty()) {
            Set<Integer> directorsId = filmFromRequest.getDirectors().stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());
            filmDB.setDirectors(directorsId);
        } else {
            filmDB.setDirectors(new HashSet<>());
        }
        return filmDB;
    }
}
