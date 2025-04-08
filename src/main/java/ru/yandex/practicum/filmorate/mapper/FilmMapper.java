package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDB;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
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
            List<Integer> genresId = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            filmDB.setGenres(genresId);
        }
        if (film.getDirectors() != null) {
            List<Integer> directorsId = film.getDirectors().stream()
                    .map(Director::getId)
                    .toList();
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
                .genres(film.getGenres())
                .mpa(film.getMpa())
                .directors(film.getDirectors())
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
        if (filmFromRequest.hasGenres()) {
            List<Integer> genresId = filmFromRequest.getGenres().stream()
                            .map(Genre::getId)
                            .toList();
            filmDB.setGenres(genresId);
        }
        if (filmFromRequest.hasMpa()) {
            filmDB.setMpa(filmFromRequest.getMpa().getId());
        }
        if (filmFromRequest.hasDirectors()) {
            List<Integer> directorsId = filmFromRequest.getDirectors().stream()
                    .map(Director::getId)
                    .toList();
            filmDB.setDirectors(directorsId);
        }
        return filmDB;
    }
}
