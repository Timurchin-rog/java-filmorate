package ru.yandex.practicum.filmorate.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FeedEventDto {
    private Long timestamp;
    private Integer userId;
    private String eventType;
    private String operation;
    private Long eventId;
    private Long entityId;
}