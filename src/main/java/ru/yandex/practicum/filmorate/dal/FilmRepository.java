package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDB;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FilmRepository extends BaseRepository<FilmDB> {
    private final GenreRepository genreRepository;
    private static final String FIND_POPULAR_FILMS = "SELECT * FROM films ORDER BY count_likes DESC LIMIT ?";
    private static final String FIND_LIKES = "SELECT user_id FROM films_likes WHERE film_id = ?";

    private static final String INSERT_FILM = "INSERT INTO films(name, description, release_date, duration, " +
            "count_likes) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_LIKE_OF_FILM = "INSERT INTO films_likes(film_id, user_id) " +
            "VALUES (?, ?)";

    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ? WHERE id = ?";
    private static final String UPDATE_MPA_FILM = "UPDATE films SET mpa = ? WHERE id = ?";

    private static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_LIKE_OF_FILM = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
    private static final String DELETE_FILM_FROM_LIKES_LIST = "DELETE FROM films_likes WHERE film_id = ?";
    private static final String DELETE_FILM_FROM_GENRES_LIST = "DELETE FROM films_genres WHERE film_id = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<FilmDB> mapper, GenreRepository genreRepository) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
    }

    public List<FilmDB> getAllFilms() {
        List<FilmDB> filmDBList = findMany("SELECT * FROM films");
        return filmDBList.stream()
                .peek(filmDB -> filmDB.setGenres(genreRepository.getGenresIdOfFilm(filmDB.getId())))
                .sorted(Comparator.comparing(FilmDB::getId))
                .collect(Collectors.toList());
    }

    public Optional<FilmDB> getFilmById(int filmId) {
        return findOne("SELECT * FROM films WHERE id = ?", filmId);
    }

    public void saveFilm(FilmDB filmDB) {
        int filmId = insert(
                INSERT_FILM,
                filmDB.getName(),
                filmDB.getDescription(),
                filmDB.getReleaseDate(),
                filmDB.getDuration(),
                filmDB.getCountLikes()
        );
        filmDB.setId(filmId);
        if (filmDB.getMpa() != null && filmDB.getMpa() != 0)
            update(UPDATE_MPA_FILM, filmDB.getMpa(), filmDB.getId());
    }

    public void updateFilm(FilmDB filmDB) {
        update(
                UPDATE_FILM,
                filmDB.getName(),
                filmDB.getDescription(),
                filmDB.getReleaseDate(),
                filmDB.getDuration(),
                filmDB.getId()
        );
    }

    public void removeFilm(int filmId) {
        delete(DELETE_FILM, filmId);
        delete(DELETE_FILM_FROM_LIKES_LIST, filmId);
        delete(DELETE_FILM_FROM_GENRES_LIST, filmId);
    }

    public void addLike(int filmId, int userId) {
        insert(
                INSERT_LIKE_OF_FILM,
                filmId,
                userId
        );
        FilmDB filmDB = getFilmById(filmId).get();
        int countLikes = filmDB.getCountLikes();
        countLikes += 1;
        update("UPDATE films SET count_likes = ? WHERE id = ?",
                countLikes,
                filmId);
    }

    public void removeLike(int filmId, int userId) {
        delete(
                DELETE_LIKE_OF_FILM,
                filmId,
                userId
        );
        FilmDB filmDB = getFilmById(filmId).get();
        int countLikes = filmDB.getCountLikes();
        if (countLikes > 0) {
            countLikes -= 1;
            update("UPDATE films SET count_likes = ? WHERE id = ?",
                    countLikes,
                    filmId);
        }
    }

    public List<FilmDB> getPopularFilms(int count) {
        return findMany(
                FIND_POPULAR_FILMS,
                count
        );
    }

    public Set<Integer> getLikesId(int filmId) {
        return findManyId(
                FIND_LIKES,
                filmId
        );
    }
}
