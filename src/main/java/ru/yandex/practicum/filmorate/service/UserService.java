package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(long userId, long friendId) {
        if (userStorage.getAllUsers().get(userId) != null) {
            if (userStorage.getAllUsers().get(friendId) != null) {
                User user = userStorage.getAllUsers().get(userId);
                user.addFriend(friendId);
                User friend = userStorage.getAllUsers().get(friendId);
                friend.addFriend(userId);
            } else {
                throw new NotFoundException(String.format("Пользователь id = %d не найден", friendId));
            }
        } else {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
    }

    public void removeFriend(long userId, long friendId) {
        if (userStorage.getAllUsers().get(userId) != null) {
            if (userStorage.getAllUsers().get(friendId) != null) {
                User user = userStorage.getAllUsers().get(userId);
                user.removeFriend(friendId);
                User friend = userStorage.getAllUsers().get(friendId);
                friend.removeFriend(userId);
            } else {
                throw new NotFoundException(String.format("Пользователь id = %d не найден", friendId));
            }
        } else {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
    }

    public List<User> findAllFriendsOfUser(long userId) {
        if (userStorage.getAllUsers().get(userId) != null) {
            User user = userStorage.getAllUsers().get(userId);
            return user.getFriends().stream()
                    .map(friendId -> userStorage.getAllUsers().get(friendId))
                    .collect(Collectors.toList());
        } else {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
    }

    public List<User> findCommonFriendsOfUsers(long userId, long otherUserId) {
        if (userStorage.getAllUsers().get(userId) != null) {
            if (userStorage.getAllUsers().get(otherUserId) != null) {
                User user = userStorage.getAllUsers().get(userId);
                User otherUser = userStorage.getAllUsers().get(otherUserId);
                return user.getFriends().stream()
                        .filter(friendId -> otherUser.getFriends().contains(friendId))
                        .map(friendId -> userStorage.getAllUsers().get(friendId))
                        .collect(Collectors.toList());
            } else {
                throw new NotFoundException(String.format("Пользователь id = %d не найден", otherUserId));
            }
        } else {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
    }
}
