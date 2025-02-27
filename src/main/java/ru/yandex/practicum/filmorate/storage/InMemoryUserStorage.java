package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Map<Long, User> getAllUsers() {
        return users;
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User createUser(User user) {
        if (checkDublicatedEmail(user)) {
            throw new ValidationException("Имейл уже используется");
        }
        checkValidation(user);
        user.setId(getNextId());
        log.info(String.format("Валидация нового пользователя id = %d пройдена", user.getId()));
        users.put(user.getId(), user);
        log.info(String.format("Новый пользователь id = %d добавлен", user.getId()));
        return user;
    }

    public User updateUser(long userId, User newUser) {
        if (users.get(userId) != null) {
            if (checkDublicatedEmail(newUser)) {
                throw new ValidationException("Имейл уже используется");
            }
            checkValidation(newUser);
            log.info(String.format("Валидация обновлённого пользователя id = %d пройдена", userId));
            User oldUser = users.get(userId);
            newUser.setId(oldUser.getId());
            newUser.setFriends(oldUser.getFriends());
            users.put(newUser.getId(), newUser);
            log.info(String.format("Пользователь id = %d обновлён", userId));
            return newUser;
        } else {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
    }

    public void removeUser(long userId) {
        users.remove(userId);
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean checkDublicatedEmail(User user) {
        return users.values().stream()
                .anyMatch(userEach -> userEach.getEmail().equals(user.getEmail()));
    }

    private void checkValidation(User user) {
        if (user.getEmail().contains(" ")) {
            throw new ValidationException("Имейл не должен содержать пробелы");
        }
        if (!user.getEmail().contains("@")) {
            throw new ValidationException("Имейл должен содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank()) {
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null) {
            throw new ValidationException("Дата рождения не может быть пустой");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
