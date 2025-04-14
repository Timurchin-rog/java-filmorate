package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.sql.Timestamp;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FeedEvent {
    Long eventId;
    Integer actorUserId;
    Integer affectedUserId;
    String eventType;
    String operation;
    Long entityId;
    Timestamp timestamp;
}