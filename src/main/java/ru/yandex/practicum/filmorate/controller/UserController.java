package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EmptyIdException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Getter
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        checkValidation(user);
        user.setId(getNextId());
        log.info(String.format("Валидация нового пользователя id = %d пройдена", user.getId()));
        users.put(user.getId(), user);
        log.info(String.format("Новый пользователь id = %d добавлен", user.getId()));
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            throw new EmptyIdException("Id должен быть указан");
        }
        if (users.get(user.getId()) != null) {
            checkValidation(user);
            log.info(String.format("Валидация обновлённого пользователя id = %d пройдена", user.getId()));
            users.put(user.getId(), user);
            log.info(String.format("Пользователь id = %d обновлён", user.getId()));
            return user;
        } else {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", user.getId()));
        }
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void checkValidation(User user) {
        if (user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может содержать пробелы");
        }
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}