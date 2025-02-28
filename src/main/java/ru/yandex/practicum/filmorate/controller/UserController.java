package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UserStorage userStorage;

    @GetMapping
    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        return userStorage.createUser(user);
    }

    @PutMapping()
    public User updateUser(@RequestBody User newUser) {
        return userStorage.updateUser(newUser);
    }

    @DeleteMapping("/{userId}")
    public void removeUser(@PathVariable long userId) {
        userStorage.removeUser(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void removeFriend(@PathVariable long userId, @PathVariable long friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> findAllFriendsOfUser(@PathVariable long userId) {
        return userService.findAllFriendsOfUser(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherUserId}")
    public List<User> findCommonFriendsOfUsers(@PathVariable long userId, @PathVariable long otherUserId) {
        return userService.findCommonFriendsOfUsers(userId, otherUserId);
    }
}