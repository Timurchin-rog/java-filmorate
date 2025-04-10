package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.EventRowMapper;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String INSERT_EVENT =
            "INSERT INTO user_feeds (user_id, event_type, operation, entity_id, event_timestamp) " +
                    "VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_EVENTS_BY_USER_ID =
            "SELECT * FROM user_feeds WHERE user_id = ? ORDER BY event_id ASC";


    public void addEvent(Event event) {
        if (event.getUserId() == null || event.getEventType() == null
                || event.getOperation() == null || event.getEntityId() == null) {
            throw new IllegalArgumentException("Некорректные данные события");
        }
        jdbcTemplate.update(
                INSERT_EVENT,
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId(),
                event.getTimestamp()
        );
    }

    public List<Event> getEventsByUserId(int userId) {
        return jdbcTemplate.query(SELECT_EVENTS_BY_USER_ID, new EventRowMapper(), userId);
    }
}