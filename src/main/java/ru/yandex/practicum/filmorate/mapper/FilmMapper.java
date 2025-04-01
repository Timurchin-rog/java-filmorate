package ru.yandex.practicum.filmorate.mapper;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dto.FilmDB;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class FilmMapper {
    public static FilmDB mapToFilmDB(Film film) {
        FilmDB filmDB = FilmDB.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .release_date(film.getReleaseDate())
                .duration(film.getDuration())
                .count_likes(film.getCountLikes())
                .build();
        if (film.getMpa() != null)
            filmDB.setMpa(film.getMpa().getId());
        if (film.getGenres() != null) {
            List<Integer> genres = film.getGenres().stream()
                    .map(Genre::getId)
                    .collect(Collectors.toList());
            filmDB.setGenres(genres);
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
                .build();
    }

    public static FilmDB updateFilmFields(FilmDB film, Film filmFromRequest) {
        if (filmFromRequest.hasName()) {
            film.setName(filmFromRequest.getName());
        }
        if (filmFromRequest.hasDescription()) {
            film.setDescription(filmFromRequest.getDescription());
        }
        if (filmFromRequest.hasReleaseDate()) {
            film.setRelease_date(filmFromRequest.getReleaseDate());
        }
        if (filmFromRequest.hasDuration()) {
            film.setDuration(filmFromRequest.getDuration());
        }
        if (filmFromRequest.hasGenres()) {
            List<Integer> genresId = filmFromRequest.getGenres().stream()
                            .map(Genre::getId)
                            .toList();
            film.setGenres(genresId);
        }
        if (filmFromRequest.hasMpa()) {
            film.setMpa(filmFromRequest.getMpa().getId());
        }
        return film;
    }
}
