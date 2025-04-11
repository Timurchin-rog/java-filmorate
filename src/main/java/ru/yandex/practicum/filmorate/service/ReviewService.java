package ru.yandex.practicum.filmorate.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.ReviewRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final FilmRepository filmRepository;
    private final FeedRepository feedRepository;
    private final UserService userService;

    @Transactional
    public Review create(Review review) {
        checkUserExists(review.getUserId());
        checkFilmExists(review.getFilmId());
        review.setUseful(0);
        Review savedReview = reviewRepository.save(review);
        feedRepository.save(FeedEvent.builder()
                .actorUserId(review.getUserId().intValue())
                .affectedUserId(review.getUserId().intValue())
                .eventType("REVIEW")
                .operation("ADD")
                .entityId(savedReview.getReviewId())
                .build());
        return savedReview;
    }

    @Transactional
    public Review update(Review review) {
        Review existing = reviewRepository.findById(review.getReviewId());
        Review updatedReview = reviewRepository.update(review);
        feedRepository.save(FeedEvent.builder()
                .actorUserId(updatedReview.getUserId().intValue())
                .affectedUserId(updatedReview.getUserId().intValue())
                .eventType("REVIEW")
                .operation("UPDATE")
                .entityId(updatedReview.getReviewId())
                .build());
        return updatedReview;
    }

    @Transactional
    public void delete(Long reviewId) {
        Review review = reviewRepository.findById(reviewId);
        feedRepository.save(FeedEvent.builder()
                .actorUserId(review.getUserId().intValue())
                .affectedUserId(review.getUserId().intValue())
                .eventType("REVIEW")
                .operation("REMOVE")
                .entityId(reviewId)
                .build());
        reviewRepository.delete(reviewId);
    }

    public Review getById(Long reviewId) {
        return reviewRepository.findById(reviewId);
    }

    public List<Review> getReviews(Long filmId, int count) {
        return filmId == null ?
                reviewRepository.findAll(count) :
                reviewRepository.findByFilmId(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        processRating(reviewId, userId, 1);
    }

    public void addDislike(Long reviewId, Long userId) {
        processRating(reviewId, userId, -1);
    }

    public void removeLike(Long reviewId, Long userId) {
        removeRating(reviewId, userId, 1);
    }

    public void removeDislike(Long reviewId, Long userId) {
        removeRating(reviewId, userId, -1);
    }

    private void processRating(Long reviewId, Long userId, int rating) {
        checkUserExists(userId);
        Review review = reviewRepository.findById(reviewId);

        Integer existingRating = reviewRepository.getUserRating(reviewId, userId);
        if (existingRating != null) {
            if (existingRating == rating) return;
            reviewRepository.updateUseful(reviewId, rating - existingRating);
        } else {
            reviewRepository.updateUseful(reviewId, rating);
        }
        reviewRepository.addRating(reviewId, userId, rating);
    }

    private void removeRating(Long reviewId, Long userId, int rating) {
        checkUserExists(userId);
        Review review = reviewRepository.findById(reviewId);

        Integer existingRating = reviewRepository.getUserRating(reviewId, userId);
        if (existingRating != null && existingRating == rating) {
            reviewRepository.updateUseful(reviewId, -rating);
            reviewRepository.removeRating(reviewId, userId);
        }
    }

    private void checkUserExists(Long userId) {
        if (userRepository.getUserById(userId.intValue()) == null) {
            throw new NotFoundException();
        }
    }

    private void checkFilmExists(Long filmId) {
        if (filmRepository.getFilmById(filmId.intValue()) == null) {
            throw new NotFoundException();
        }
    }

}