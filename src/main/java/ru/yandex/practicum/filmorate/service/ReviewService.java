package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        return reviewRepository.save(review);
    }

    public Review update(Review review) {
        return reviewRepository.update(review);
    }

    public void delete(Long reviewId) {
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
        reviewRepository.addRating(reviewId, userId, 1);
        reviewRepository.updateUseful(reviewId, 1);
    }

    public void addDislike(Long reviewId, Integer userId) {
        reviewRepository.addRating(reviewId, userId, -1);
        reviewRepository.updateUseful(reviewId, -1);
    }

    public void removeLike(Long reviewId, Integer userId) {
        reviewRepository.removeRating(reviewId, userId);
        reviewRepository.updateUseful(reviewId, -1);
    }

    public void removeDislike(Long reviewId, Integer userId) {
        reviewRepository.removeRating(reviewId, userId);
        reviewRepository.updateUseful(reviewId, 1);
    }
}