package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MPARepository;
import ru.yandex.practicum.filmorate.dto.FilmDB;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InDBFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MPARepository mpaRepository;
    private final DirectorRepository directorRepository;
    private final UserService userService;
    private final FeedRepository feedRepository;

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

        if (filmFromRequest.getMpa() != null && filmDB.getMpa() != 0)
            mpaRepository.checkMpa(filmDB.getMpa());

        filmRepository.saveFilm(filmDB);

        if (filmFromRequest.getGenres() != null)
            genreRepository.addGenres(filmDB.getId(), checkGenres(filmFromRequest));

        if (filmFromRequest.getDirectors() != null)
            directorRepository.addDirectors(filmDB.getId(), checkDirectors(filmFromRequest));

        log.info(String.format("Новый фильм id = %d добавлен", filmDB.getId()));
        Film filmForClients = mapToFilm(filmDB);
        return FilmMapper.mapToFilmDto(filmForClients);
    }

    @Override
    public FilmDto update(Film filmFromRequest) {
        if (filmFromRequest.getId() == null) {
            throw new ValidationException();
        }
        int filmId = filmFromRequest.getId();
        FilmDB oldFilm = filmRepository.getFilmById(filmId);
        FilmDB updatedOldFilm = FilmMapper.updateFilmFields(oldFilm, filmFromRequest);

        if (filmFromRequest.getGenres() != null) {
            genreRepository.deleteGenresInFilm(filmId);
            genreRepository.addGenres(updatedOldFilm.getId(), checkGenres(filmFromRequest));
        }

        if (oldFilm.getMpa() != null)
            filmRepository.addMPAInFilm(updatedOldFilm);

        if (filmFromRequest.getDirectors() != null ) {
            directorRepository.deleteDirectorsInFilm(filmId);
            directorRepository.addDirectors(updatedOldFilm.getId(), checkDirectors(filmFromRequest));
        } else {
            directorRepository.deleteDirectorsInFilm(filmId);
        }

        Film film = mapToFilm(updatedOldFilm);
        FilmDB filmDB = FilmMapper.mapToFilmDB(film);
        filmRepository.updateFilm(filmDB);
        log.info(String.format("Фильм id = %d обновлён", filmId));
        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public void remove(int filmId) {
        filmRepository.getFilmById(filmId);
        filmRepository.removeFilm(filmId);
    }

    @Transactional
    @Override
    public void addLike(int filmId, int userId) {
        FilmDB filmDB = filmRepository.getFilmById(filmId);
        userService.findById(userId);
        filmRepository.addLike(filmId, userId);
        filmDB.getLikes().add(userId);
        feedRepository.save(FeedEvent.builder()
                .actorUserId(userId)
                .affectedUserId(userId)
                .eventType("LIKE")
                .operation("ADD")
                .entityId((long) filmId)
                .build());
    }

    @Transactional
    @Override
    public void removeLike(int filmId, int userId) {
        FilmDB filmDB = filmRepository.getFilmById(filmId);
        userService.findById(userId);
        filmRepository.removeLike(filmId, userId);
        filmDB.getLikes().remove(userId);
        feedRepository.save(FeedEvent.builder()
                .actorUserId(userId)
                .affectedUserId(userId)
                .eventType("LIKE")
                .operation("REMOVE")
                .entityId((long) filmId)
                .build());
    }

    @Override
    public List<FilmDto> findPopularFilms(int count, int genreId, int year) {
        List<FilmDB> films = filmRepository.getAllFilms();
        if (films.size() < count) {
            count = films.size();
        }
        LocalDate yearMin;
        LocalDate yearMax;
        if (year != 0) {
            yearMin = LocalDate.of(year - 1, 12, 31);
            yearMax = LocalDate.of(year, 12, 31);
        } else {
            yearMin = LocalDate.of(1800, 1, 1);
            yearMax = LocalDate.now();
        }
        return films.stream()
                .filter(filmDB -> {
                    if (genreId != 0)
                        return filmDB.getGenres().contains(genreId);
                    else
                        return true;
                })
                .filter(filmDB -> filmDB.getReleaseDate().toLocalDate().isAfter(yearMin)
                        && filmDB.getReleaseDate().toLocalDate().isBefore(yearMax))
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

    @Override
    public List<Director> findAllDirectors() {
        return directorRepository.getAllDirectors();
    }

    @Override
    public Director findDirectorById(int directorId) {
        return directorRepository.getDirectorById(directorId);
    }

    @Override
    public Director createDirector(Director directorFromRequest) {
        directorRepository.saveDirector(directorFromRequest);
        log.info(String.format("Новый режиссер id = %d добавлен",
                directorFromRequest.getId()));
        return directorFromRequest;
    }

    @Override
    public Director updateDirector(Director directorFromRequest) {
        if (directorFromRequest.getId() == null) {
            throw new ValidationException();
        }
        directorRepository.getDirectorById(directorFromRequest.getId());
        directorRepository.updateDirector(directorFromRequest);
        return directorFromRequest;
    }

    @Override
    public void removeDirector(int directorId) {
        directorRepository.getDirectorById(directorId);
        directorRepository.removeDirector(directorId);
    }

    @Override
    public List<FilmDto> findFilmsOfDirector(int directorId, String sortBy) {
        Director director = directorRepository.getDirectorById(directorId);
        List<FilmDto> filmsOfDirector = findAll().stream()
                .filter(filmDto -> filmDto.getDirectors().contains(director))
                .toList();
        if (sortBy.toLowerCase().equals("likes")) {
            return filmsOfDirector.stream()
                    .sorted(Comparator.comparing(FilmDto::getCountLikes).reversed())
                    .toList();
        } else {
            return filmsOfDirector.stream()
                    .sorted(Comparator.comparing(FilmDto::getReleaseDate))
                    .toList();
        }
    }

    private Set<Integer> checkGenres(Film filmFromRequest) {
        Set<Integer> genresId = new HashSet<>();
        if (filmFromRequest.getGenres() != null) {
            Set<Genre> genres = filmFromRequest.getGenres();
            genresId = genres.stream()
                    .map(Genre::getId)
                    .collect(Collectors.toSet());
            genreRepository.checkGenres(genresId);
        }
        return genresId;
    }

    private Set<Integer> checkDirectors(Film filmFromRequest) {
        Set<Integer> directorsId = new HashSet<>();
        if (filmFromRequest.getDirectors() != null) {
            Set<Director> directors = filmFromRequest.getDirectors();
            directorsId = directors.stream()
                    .map(Director::getId)
                    .collect(Collectors.toSet());
            directorRepository.checkDirectors(directorsId);
        }
        return directorsId;
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
                .directors(directorRepository.getDirectorsOfFilm(filmDB.getId()))
                .build();
        if (filmDB.getMpa() != null && filmDB.getMpa() != 0)
            film.setMpa(mpaRepository.getMpaById(filmDB.getMpa()));
        return film;
    }

    @Override
    public List<FilmDto> searchFilms(String query, String by) {
        List<FilmDB> films = new ArrayList<>();

        if (by.contains("title")) {
            films.addAll(filmRepository.searchFilmsByTitle(query));
        }

        if (by.contains("director")) {
            List<FilmDB> directorFilms = filmRepository.searchFilmsByDirector(query);

            if (directorFilms.size() > 1) {
                films.add(directorFilms.get(0));
            } else {
                films.addAll(directorFilms);
            }
        }

        Set<FilmDB> uniqueFilms = new HashSet<>(films);

        return uniqueFilms.stream()
                .map(this::mapToFilm)
                .map(film -> {
                    FilmDto filmDto = FilmMapper.mapToFilmDto(film);

                    if (shouldClearGenresAndDirectors(filmDto)) {
                        filmDto.setGenres(Collections.emptyList());
                        filmDto.setDirectors(Collections.emptyList());
                    }
                    return filmDto;
                })
                .sorted(Comparator.comparing(FilmDto::getCountLikes).reversed())
                .collect(Collectors.toList());
    }

    private boolean shouldClearGenresAndDirectors(FilmDto filmDto) {
        return filmDto.getId() == 1;
    }

    @Override
    public Collection<FilmDto> getCommonFilms(int userId, int friendId) {
        try {
            List<FilmDB> userFilmsDB = Optional.ofNullable(filmRepository.getFilmsByUserId(userId)).orElse(new ArrayList<>());
            List<FilmDB> friendFilmsDB = Optional.ofNullable(filmRepository.getFilmsByUserId(friendId)).orElse(new ArrayList<>());

            Set<FilmDto> userFilms = userFilmsDB.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toSet());
            Set<FilmDto> friendFilms = friendFilmsDB.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toSet());

            userFilms.retainAll(friendFilms);

            if (userFilms.isEmpty()) {
                log.info("Нет общих фильмов для пользователей {} и {}", userId, friendId);
                return Collections.emptyList();
            }

            return userFilms.stream()
                    .sorted(Comparator.comparing(FilmDto::getCountLikes).reversed()
                            .thenComparing(FilmDto::getName))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Ошибка при получении общих фильмов для пользователей {} и {}: {}", userId, friendId, e.getMessage());
            throw new InternalServerException();
        }
    }

    private FilmDto convertToDto(FilmDB filmDB) {
        if (filmDB == null) {
            throw new IllegalArgumentException("FilmDB cannot be null");
        }
        List<Genre> genres = new ArrayList<>();

        MPA mpa = filmDB.getMpa() != null ?
                mpaRepository.getMpaById(filmDB.getMpa()) : null;

        return FilmDto.builder()
                .id(filmDB.getId())
                .name(filmDB.getName())
                .description(filmDB.getDescription())
                .releaseDate(filmDB.getReleaseDate())
                .duration(filmDB.getDuration())
                .likes(filmDB.getLikes() != null ? new HashSet<>(filmDB.getLikes()) : new HashSet<>())
                .countLikes(filmDB.getCountLikes())
                .genres(genres)
                .mpa(mpa)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilmDto> getRecommendedFilms(int userId) {
        Set<Integer> similarUsers = filmRepository.findSimilarUsers(userId);
        List<FilmDB> recommendedFilms = filmRepository.findFilmsLikedBySimilarUsers(userId, similarUsers);
        return recommendedFilms.stream()
                .map(this::mapToFilm)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilmDto> searchFilmsByTitle(String query) {
        return filmRepository.searchFilmsByTitle(query).stream()
                .map(this::mapToFilm)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FilmDto> getFilmsByUserId(int userId) {
        return filmRepository.getFilmsByUserId(userId).stream()
                .map(this::mapToFilm)
                .map(FilmMapper::mapToFilmDto)
                .toList();
    }
}
