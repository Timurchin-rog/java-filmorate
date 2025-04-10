package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {
    private Long reviewId;

    @NotBlank(message = "Содержание отзыва не может быть пустым")
    private String content;

    @NotNull(message = "Поле isPositive обязательно")
    private Boolean isPositive;

    @NotNull(message = "userId обязательно")
    private Long userId;

    @NotNull(message = "filmId обязательно")
    private Long filmId;

    private Integer useful;
}