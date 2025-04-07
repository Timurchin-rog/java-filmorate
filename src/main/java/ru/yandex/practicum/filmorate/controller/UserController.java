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
    private final String pathFriends = "/{user-id}/friends/{friend-id}";
    private final String pathFriend = "/{user-id}/friends";

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{user-id}")
    public UserDto findById(@PathVariable(name = "user-id") int userId) {
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

    @DeleteMapping("/{user-id}")
    public String remove(@PathVariable(name = "user-id") int userId) {
        return userService.remove(userId);
    }

    @PutMapping(pathFriends)
    public void addFriend(@PathVariable(name = "user-id") int userId,
                          @PathVariable(name = "friend-id") int friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping(pathFriends)
    public void removeFriend(@PathVariable(name = "user-id") int userId,
                             @PathVariable(name = "friend-id") int friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping(pathFriend)
    public List<UserDto> findAllFriends(@PathVariable(name = "user-id") int userId) {
        return userService.findAllFriends(userId);
    }

    @GetMapping(pathFriend + "/common/{other-user-id}")
    public List<UserDto> findCommonFriends(@PathVariable(name = "user-id") int userId,
                                           @PathVariable(name = "other-user-id") int otherUserId) {
        return userService.findCommonFriends(userId, otherUserId);
    }
}