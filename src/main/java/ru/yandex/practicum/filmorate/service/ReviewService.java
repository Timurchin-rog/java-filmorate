package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;

    public Review create(Review review) {
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        return reviewRepository.save(review);
    }

    public Review update(Review review) {
        validateReviewExists(review.getReviewId());
        return reviewRepository.update(review);
    }

    public void delete(Long reviewId) {
        validateReviewExists(reviewId);
        reviewRepository.delete(reviewId);
    }

    public Review getById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public List<Review> getReviews(Integer filmId, int count) {
        if (filmId == null) {
            return reviewRepository.findAll(count);
        } else {
            return reviewRepository.findByFilmId(filmId, count);
        }
    }

    public void addLike(Long reviewId, Integer userId) {
        checkUserAndReviewExists(userId, reviewId);
        reviewRepository.addRating(reviewId, userId, 1);
        reviewRepository.updateUseful(reviewId, 1);
    }

    public void addDislike(Long reviewId, Integer userId) {
        checkUserAndReviewExists(userId, reviewId);
        reviewRepository.addRating(reviewId, userId, -1);
        reviewRepository.updateUseful(reviewId, -1);
    }

    public void removeLike(Long reviewId, Integer userId) {
        checkUserAndReviewExists(userId, reviewId);
        reviewRepository.removeRating(reviewId, userId);
        reviewRepository.updateUseful(reviewId, -1);
    }

    public void removeDislike(Long reviewId, Integer userId) {
        checkUserAndReviewExists(userId, reviewId);
        reviewRepository.removeRating(reviewId, userId);
        reviewRepository.updateUseful(reviewId, 1);
    }

    private void validateUserAndFilm(Integer userId, Integer filmId) {
        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException(String.format("Отзыв с id = %d не найден", userId));
        }
        if (filmRepository.getFilmById(filmId) == null) {
            throw new NotFoundException(String.format("Фильм с id = %d не найден", filmId));
        }
    }

    private void checkUserAndReviewExists(Integer userId, Long reviewId) {
        if (userRepository.getUserById(userId) == null) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
        if (reviewRepository.findById(reviewId) == null) {
            throw new NotFoundException(String.format("Отзыв с id = %d не найден", reviewId));
        }
    }

    private void validateReviewExists(Long reviewId) {
        if (reviewRepository.findById(reviewId) == null) {
            throw new NotFoundException(String.format("Отзыв с id = %d не найден", reviewId));
        }
    }
}