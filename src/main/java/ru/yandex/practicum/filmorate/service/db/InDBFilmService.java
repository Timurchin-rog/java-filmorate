package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MPARepository;
import ru.yandex.practicum.filmorate.dto.FilmDB;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InDBFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MPARepository mpaRepository;
    private final UserService userService;

    @Override
    public List<FilmDto> findAll() {
        return filmRepository.getAllFilms().stream()
                .map(this::mapToFilm)
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    @Override
    public FilmDto findById(int filmId) {
        FilmDB filmDB = filmRepository.getFilmById(filmId);
        Film film = mapToFilm(filmDB);
        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public FilmDto create(Film filmFromRequest) {
        Film newFilm = filmFromRequest.toBuilder().build();
        FilmDB filmDB = FilmMapper.mapToFilmDB(newFilm);
        Set<Integer> genresId = new HashSet<>();
        if (filmFromRequest.getGenres() != null) {
            List<Genre> genres = filmFromRequest.getGenres();
            genresId = genres.stream()
                            .map(Genre::getId)
                            .collect(Collectors.toSet());
            genreRepository.checkGenres(genresId);
        }
        if (filmFromRequest.getMpa() != null && filmDB.getMpa() != 0)
            mpaRepository.checkMpa(filmDB.getMpa());
        filmRepository.saveFilm(filmDB);
        genreRepository.addGenres(filmDB.getId(), genresId);
        log.info(String.format("Новый фильм id = %d добавлен", filmDB.getId()));
        Film filmForClients = mapToFilm(filmDB);
        return FilmMapper.mapToFilmDto(filmForClients);
    }

    @Override
    public FilmDto update(Film filmFromRequest) {
        if (filmFromRequest.getId() == null) {
            throw new ValidationException("При обновлении фильма не указан id");
        }
        int filmId = filmFromRequest.getId();
        FilmDB oldFilm = filmRepository.getFilmById(filmId);
        FilmDB updatedOldFilm = FilmMapper.updateFilmFields(oldFilm, filmFromRequest);
        Film film = mapToFilm(updatedOldFilm);
        FilmDB filmDB = FilmMapper.mapToFilmDB(film);
        filmRepository.updateFilm(filmDB);
        log.info(String.format("Фильм id = %d обновлён", filmId));
        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public String remove(int filmId) {
        filmRepository.getFilmById(filmId);
        filmRepository.removeFilm(filmId);
        return String.format("Фильм id = %d удалён", filmId);
    }

    @Override
    public void addLike(int filmId, int userId) {
        FilmDB filmDB = filmRepository.getFilmById(filmId);
        userService.findById(userId);
        filmRepository.addLike(filmId, userId);
        filmDB.getLikes().add(userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        FilmDB filmDB = filmRepository.getFilmById(filmId);
        userService.findById(userId);
        filmRepository.removeLike(filmId, userId);
        filmDB.getLikes().remove(userId);
    }

    @Override
    public List<FilmDto> findPopularFilms(int count) {
        List<FilmDB> films = filmRepository.getAllFilms();
        if (films.size() < count) {
            count = films.size();
        }
        return films.stream()
                .map(this::mapToFilm)
                .map(FilmMapper::mapToFilmDto)
                .sorted(Comparator.comparing(FilmDto::getCountLikes).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public List<Genre> findAllGenres() {
        return genreRepository.getAllGenres();
    }

    @Override
    public Genre findGenreById(int genreId) {
        return genreRepository.getGenreById(genreId);
    }

    @Override
    public List<MPA> findAllMPA() {
        return mpaRepository.getAllMPA();
    }

    @Override
    public MPA findMPAById(int mpaId) {
        return mpaRepository.getMpaById(mpaId);
    }

    private Film mapToFilm(FilmDB filmDB) {
        Film film = Film.builder()
                .id(filmDB.getId())
                .name(filmDB.getName())
                .description(filmDB.getDescription())
                .releaseDate(filmDB.getReleaseDate())
                .duration(filmDB.getDuration())
                .likes(filmDB.getLikes())
                .countLikes(filmDB.getCountLikes())
                .genres(genreRepository.getGenresOfFilm(filmDB.getId()))
                .build();
        if (filmDB.getMpa() != null && filmDB.getMpa() != 0)
            film.setMpa(mpaRepository.getMpaById(filmDB.getMpa()));
        return film;
    }
}
