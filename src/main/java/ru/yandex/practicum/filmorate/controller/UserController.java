package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FeedEventDto;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.db.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FeedService feedService;
    private final FilmService filmService;
    private final String pathFriends = "/{id}/friends/{friend-id}";
    private final String pathFriend = "/{id}/friends";

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable int id) {
        return userService.findById(id);
    }

    @PostMapping
    public UserDto create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping()
    public UserDto update(@RequestBody User newUser) {
        return userService.update(newUser);
    }

    @DeleteMapping("/{id}")
    public void remove(@PathVariable int id) {
        userService.remove(id);
    }

    @PutMapping(pathFriends)
    public void addFriend(@PathVariable int id,
                          @PathVariable(name = "friend-id") int friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(pathFriends)
    public void removeFriend(@PathVariable int id,
                             @PathVariable(name = "friend-id") int friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping(pathFriend)
    public List<UserDto> findAllFriends(@PathVariable int id) {
        return userService.findAllFriends(id);
    }

    @GetMapping(pathFriend + "/common/{other-user-id}")
    public List<UserDto> findCommonFriends(@PathVariable int id,
                                           @PathVariable(name = "other-user-id") int otherUserId) {
        return userService.findCommonFriends(id, otherUserId);
    }

    @GetMapping("/{id}/feed")
    public List<FeedEventDto> getFeed(@PathVariable int id) {
        return feedService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<FilmDto> getRecommendations(@PathVariable int id) {
        return filmService.getRecommendedFilms(id);
    }
}
