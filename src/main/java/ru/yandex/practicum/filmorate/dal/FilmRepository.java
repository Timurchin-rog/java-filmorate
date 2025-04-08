package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDB;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmRepository extends BaseRepository<FilmDB> {
    private final GenreRepository genreRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbc);
    }


    public List<FilmDB> getAllFilms() {
        List<FilmDB> filmDBList = findMany("SELECT * FROM films");
        return filmDBList.stream()
                .peek(filmDB -> filmDB.setGenres(genreRepository.getGenresIdOfFilm(filmDB.getId())))
                .sorted(Comparator.comparing(FilmDB::getId))
                .collect(Collectors.toList());
    }

    public FilmDB getFilmById(int filmId) {
        Optional<FilmDB> filmOpt = findOne("SELECT * FROM films WHERE id = ?", filmId);
        if (filmOpt.isEmpty())
            throw new NotFoundException(String.format("Фильм id = %d не найден", filmId));
        filmOpt.get().setLikes(getLikesId(filmId));
        return filmOpt.get();
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
        delete(DELETE_FILM_FROM_LIKES_LIST, filmId);
        delete(DELETE_FILM_FROM_GENRES_LIST, filmId);
        delete(DELETE_FILM, filmId);
    }

    public void addLike(int filmId, int userId) {
        insert(
                INSERT_LIKE_OF_FILM,
                filmId,
                userId
        );
        FilmDB filmDB = getFilmById(filmId);
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
        FilmDB filmDB = getFilmById(filmId);
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

    // Находим пользователей с общими лайками
    public Set<Integer> findSimilarUsers(int userId) {
        String sql = """
                SELECT DISTINCT fl2.user.id
                FROM films_likes fl1
                JOIN films_likes fl2 ON fl1.film_id = fl2.film_id
                WHERE fl1.user_id = ? AND fl2.user_id !=?
                GROUP BY fl2.user_id
                ORDER BY COUNT(fl2.film_id) DESC
                LIMIT 5
                """;
        return new HashSet<>(jdbc.queryForList(sql, Integer.class, userId, userId));

    }

    // находим фильмы с общими лайками
    public List<FilmDB> findFilmsLikedBySimilarUsers(int userId, Set<Integer> similarUsers) {
        if (similarUsers.isEmpty()) {
            return List.of();
        }
        String sql = """
                SELECT f.* FROM films f
                JOIN films_likes fl ON f.id = fl.film_id
                WHERE fl.user_id IN (:similarUsers)
                AND f.id NOT IN (SELECT film_id FROM films_likes WHERE user_id = ?)
                GROUP BY f.id
                ORDER BY COUNT(fl.user_id) DESC
                LIMIT 10
                """;
        SqlParameterSource params = new MapSqlParameterSource()
                .addValue("similarUsers", similarUsers)
                .addValue("userId", userId);

        return namedParameterJdbcTemplate.query(sql, params, mapper);

    }
}
