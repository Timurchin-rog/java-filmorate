package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FeedEventDto;
import ru.yandex.practicum.filmorate.dal.repository.FeedRepository;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserService userService;

    public List<FeedEventDto> getFeed(int userId) {
        userService.findById(userId);
        return feedRepository.findByAffectedUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private FeedEventDto convertToDto(FeedEvent event) {
        return FeedEventDto.builder()
                .eventId(event.getEventId())
                .userId(event.getActorUserId())
                .entityId(event.getEntityId())
                .eventType(event.getEventType())
                .operation(event.getOperation())
                .timestamp(event.getTimestamp().getTime())
                .build();
    }
}