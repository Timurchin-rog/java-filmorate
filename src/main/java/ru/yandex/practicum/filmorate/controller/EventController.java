package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.dal.EventRepository;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class EventController {
    private final EventRepository eventRepository;

    @GetMapping("/{userId}/feed")
    public List<Event> getFeed(@PathVariable int userId) {
        return eventRepository.getEventsByUserId(userId);
    }
}