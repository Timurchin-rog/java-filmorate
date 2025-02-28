package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    Map<Long, User> getAllUsers();

    List<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    void removeUser(long userId);
}
