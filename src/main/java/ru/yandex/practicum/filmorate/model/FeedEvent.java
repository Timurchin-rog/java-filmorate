package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import java.sql.Timestamp;

@Data
@Builder
public class FeedEvent {
    private Long eventId;
    private Integer actorUserId;
    private Integer affectedUserId;
    private String eventType;
    private String operation;
    private Long entityId;
    private Timestamp timestamp;
}