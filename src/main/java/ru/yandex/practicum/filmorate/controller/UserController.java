package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final String pathFriends = "/{userId}/friends/{friendId}";
    private final String pathFriend = "/{userId}/friends";

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserDto findById(@PathVariable int userId) {
        return userService.findById(userId);
    }

    @PostMapping
    public UserDto create(@RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping()
    public UserDto update(@RequestBody User newUser) {
        return userService.update(newUser);
    }

    @DeleteMapping("/{userId}")
    public void remove(@PathVariable int userId) {
        userService.remove(userId);
    }

    @PutMapping(pathFriends)
    public void addFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping(pathFriends)
    public void removeFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping(pathFriend)
    public List<UserDto> findAllFriends(@PathVariable int userId) {
        return userService.findAllFriends(userId);
    }

    @GetMapping(pathFriend + "/common/{otherUserId}")
    public List<UserDto> findCommonFriends(@PathVariable int userId, @PathVariable int otherUserId) {
        return userService.findCommonFriends(userId, otherUserId);
    }
}