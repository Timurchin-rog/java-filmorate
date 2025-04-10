package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.FilmDB;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FilmRepository extends BaseRepository<FilmDB> {
    private final GenreRepository genreRepository;
    private final DirectorRepository directorRepository;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String FIND_POPULAR_FILMS = "SELECT * FROM films ORDER BY count_likes DESC LIMIT ?";
    private static final String FIND_LIKES = "SELECT user_id FROM films_likes WHERE film_id = ?";
    private static final String SEARCH_FILMS_BY_TITLE = "SELECT * FROM films WHERE LOWER(name) LIKE LOWER(?)";
    private static final String SEARCH_FILMS_BY_DIRECTOR = "SELECT f.* FROM films AS f " +
            "JOIN films_directors AS fd ON f.id = fd.film_id " +
            "JOIN directors AS d ON fd.director_id = d.id " +
            "WHERE LOWER(d.name) LIKE LOWER(?)";
    private static final String FIND_FILMS_BY_USER_ID = "SELECT f.* FROM films f JOIN films_likes fl ON f.id = fl.film_id WHERE fl.user_id = ?";
    private static final String INSERT_FILM = "INSERT INTO films(name, description, release_date, duration, count_likes) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_LIKE_OF_FILM = "INSERT INTO films_likes(film_id, user_id) VALUES (?, ?)";
    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ? WHERE id = ?";
    private static final String UPDATE_MPA_FILM = "UPDATE films SET mpa = ? WHERE id = ?";
    private static final String DELETE_FILM = "DELETE FROM films WHERE id = ?";
    private static final String DELETE_LIKE_OF_FILM = "DELETE FROM films_likes WHERE film_id = ? AND user_id = ?";
    private static final String DELETE_FILM_FROM_LIKES_LIST = "DELETE FROM films_likes WHERE film_id = ?";
    private static final String DELETE_FILM_FROM_GENRES_LIST = "DELETE FROM films_genres WHERE film_id = ?";
    private static final String DELETE_FILM_FROM_DIRECTORS_LIST = "DELETE FROM films_directors WHERE film_id = ?";

    public FilmRepository(JdbcTemplate jdbc,
                          RowMapper<FilmDB> mapper,
                          GenreRepository genreRepository,
                          DirectorRepository directorRepository) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
        this.directorRepository = directorRepository;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbc);
    }

    public List<FilmDB> getAllFilms() {
        List<FilmDB> filmDBList = findMany("SELECT * FROM films");
        return filmDBList.stream()
                .peek(filmDB -> filmDB.setGenres(genreRepository.getGenresIdOfFilm(filmDB.getId())))
                .peek(filmDB -> filmDB.setDirectors(directorRepository.getDirectorsIdOfFilm(filmDB.getId())))
                .sorted(Comparator.comparing(FilmDB::getId))
                .collect(Collectors.toList());
    }

    public FilmDB getFilmById(int filmId) {
        Optional<FilmDB> filmOpt = findOne("SELECT * FROM films WHERE id = ?", filmId);
        if (filmOpt.isEmpty())
            throw new NotFoundException();
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
        delete(DELETE_FILM_FROM_DIRECTORS_LIST, filmId);
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

    public Set<Integer> getLikesId(int filmId) {
        return findManyId(
                FIND_LIKES,
                filmId
        );
    }

    public List<FilmDB> searchFilmsByTitle(String query) {
        String searchPattern = "%" + query + "%";
        List<FilmDB> filmDBList = findMany(SEARCH_FILMS_BY_TITLE, searchPattern);
        return filmDBList.stream()
                .peek(filmDB -> filmDB.setGenres(genreRepository.getGenresIdOfFilm(filmDB.getId())))
                .sorted(Comparator.comparing(FilmDB::getCountLikes).reversed())
                .collect(Collectors.toList());
    }

    public List<FilmDB> searchFilmsByDirector(String query) {
        String searchPattern = "%" + query + "%";
        List<FilmDB> filmDBList = findMany(SEARCH_FILMS_BY_DIRECTOR, searchPattern);
        return filmDBList.stream()
                .peek(filmDB -> filmDB.setGenres(genreRepository.getGenresIdOfFilm(filmDB.getId())))
                .sorted(Comparator.comparing(FilmDB::getCountLikes).reversed())
                .collect(Collectors.toList());
    }

    public List<FilmDB> getFilmsByUserId(int userId) {
        List<FilmDB> filmDBList = findMany(FIND_FILMS_BY_USER_ID, userId);
        return filmDBList.stream()
                .peek(filmDB -> filmDB.setGenres(genreRepository.getGenresIdOfFilm(filmDB.getId())))
                .collect(Collectors.toList());
    }

    public Set<Integer> findSimilarUsers(int userId) {
        String sql = """
                SELECT fl2.user_id
                FROM films_likes fl1
                JOIN films_likes fl2 ON fl1.film_id = fl2.film_id
                WHERE fl1.user_id = ? AND fl2.user_id != ?
                GROUP BY fl2.user_id
                ORDER BY COUNT(fl2.film_id) DESC
                LIMIT 5
                """;
        return new HashSet<>(jdbc.queryForList(sql, Integer.class, userId, userId));
    }

    public List<FilmDB> findFilmsLikedBySimilarUsers(int userId, Set<Integer> similarUsers) {
        if (similarUsers.isEmpty()) {
            return List.of();
        }
        String sql = """
                SELECT f.* FROM films f
                JOIN films_likes fl ON f.id = fl.film_id
                WHERE fl.user_id IN (:similarUsers)
                AND f.id NOT IN (SELECT film_id FROM films_likes WHERE user_id = :userId)
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
