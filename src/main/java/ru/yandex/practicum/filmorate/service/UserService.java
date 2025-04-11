package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> findAll();

    UserDto findById(int userId);

    UserDto create(User user);

    UserDto update(User user);

    void remove(int userId);

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    List<UserDto> findAllFriends(int userId);

    List<UserDto> findCommonFriends(int userId, int otherUserId);
}
