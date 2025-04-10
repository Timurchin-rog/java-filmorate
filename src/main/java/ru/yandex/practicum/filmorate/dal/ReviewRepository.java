package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ReviewRepository {
    private final JdbcTemplate jdbcTemplate;

    public Review save(Review review) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                            "VALUES (?, ?, ?, ?, ?)",
                    new String[]{"review_id"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            ps.setInt(5, review.getUseful());
            return ps;
        }, keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return review;
    }

    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        int updated = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (updated == 0) {
            throw new NotFoundException("Review not found with id: " + review.getReviewId());
        }
        return findById(review.getReviewId());
    }

    public void delete(Long reviewId) {
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", reviewId);
    }

    public Review findById(Long reviewId) {
        try {
            String sql = "SELECT * FROM reviews WHERE review_id = ?";
            return jdbcTemplate.queryForObject(sql, new ReviewRowMapper(), reviewId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Review not found with id: " + reviewId);
        }
    }

    public List<Review> findByFilmId(Long filmId, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, new ReviewRowMapper(), filmId, count);
    }

    public List<Review> findAll(int count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, new ReviewRowMapper(), count);
    }

    public void addRating(Long reviewId, Long userId, int rating) {
        String sql = "MERGE INTO review_ratings (review_id, user_id, rating_type) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, reviewId, userId, rating);
    }

    public void removeRating(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    public Integer getUserRating(Long reviewId, Long userId) {
        try {
            String sql = "SELECT rating_type FROM review_ratings WHERE review_id = ? AND user_id = ?";
            return jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void updateUseful(Long reviewId, int delta) {
        String sql = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
        jdbcTemplate.update(sql, delta, reviewId);
    }
}