package ru.yandex.practicum.filmorate.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDB {
    Integer id;
    String email;
    String login;
    String name;
    Date birthday;
    Set<Integer> friends;

    public static class UserDBBuilder {

        public UserDB.UserDBBuilder email(String email) {
            if (email.contains(" ")) {
                throw new ValidationException("Имейл не должен содержать пробелы");
            }
            if (!email.contains("@")) {
                throw new ValidationException("Имейл должен содержать @");
            }
            this.email = email;
            return this;
        }

        public UserDB.UserDBBuilder login(String login) {
            if (login == null || login.isBlank()) {
                throw new ValidationException("Логин не может быть пустым");
            }
            if (login.contains(" ")) {
                throw new ValidationException("Логин не может содержать пробелы");
            }
            this.login = login;
            return this;
        }

        public UserDB.UserDBBuilder name(String name) {
            if (name != null) {
                this.name = name;
                return this;
            }
            this.name = login;
            return this;
        }

        public UserDB.UserDBBuilder birthday(Date birthday) {
            if (birthday == null) {
                throw new ValidationException("Дата рождения не может быть пустой");
            }
            if (birthday.after(Date.from(Instant.now()))) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            this.birthday = birthday;
            return this;
        }
    }
}
