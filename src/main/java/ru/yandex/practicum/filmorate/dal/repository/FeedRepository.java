package ru.yandex.practicum.filmorate.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FeedEvent;

import java.util.List;

@Repository
public class FeedRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<FeedEvent> rowMapper = (rs, rowNum) -> FeedEvent.builder()
            .eventId(rs.getLong("event_id"))
            .actorUserId(rs.getInt("actor_user_id"))
            .affectedUserId(rs.getInt("affected_user_id"))
            .eventType(rs.getString("event_type"))
            .operation(rs.getString("operation"))
            .entityId(rs.getLong("entity_id"))
            .timestamp(rs.getTimestamp("timestamp"))
            .build();

    public FeedRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(FeedEvent event) {
        String sql = "INSERT INTO user_feeds (actor_user_id, affected_user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                event.getActorUserId(),
                event.getAffectedUserId(),
                event.getEventType(),
                event.getOperation(),
                event.getEntityId());
    }

    public void removeAllFeedsOfUser(int userId) {
        jdbcTemplate.update("DELETE FROM user_feeds WHERE actor_user_id = ? OR affected_user_id = ?", userId, userId);
    }

    public List<FeedEvent> findByAffectedUserId(int userId) {
        return jdbcTemplate.query(
                "SELECT * FROM user_feeds WHERE affected_user_id = ? ORDER BY timestamp ASC", rowMapper, userId);
    }
}