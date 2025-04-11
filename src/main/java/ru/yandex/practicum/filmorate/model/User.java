package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.sql.Date;
import java.time.Instant;
import java.util.Set;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    Integer id;
    String email;
    String login;
    String name;
    Date birthday;
    Set<Integer> friends;

    public boolean hasEmail() {
        return ! (email == null || email.isBlank());
    }

    public boolean hasLogin() {
        return ! (login == null || login.isBlank());
    }

    public boolean hasName() {
        return ! (name == null || name.isBlank());
    }

    public boolean hasBirthday() {
        return ! (birthday == null);
    }

    public static class UserBuilder {

        public User.UserBuilder email(String email) {
            if (email.contains(" ")) {
                throw new ValidationException();
            }
            if (!email.contains("@")) {
                throw new ValidationException();
            }
            this.email = email;
            return this;
        }

        public User.UserBuilder login(String login) {
            if (login == null || login.isBlank()) {
                throw new ValidationException();
            }
            if (login.contains(" ")) {
                throw new ValidationException();
            }
            this.login = login;
            return this;
        }

        public User.UserBuilder name(String name) {
            if (name != null) {
                this.name = name;
                return this;
            }
            this.name = login;
            return this;
        }

        public User.UserBuilder birthday(Date birthday) {
            if (birthday == null) {
                throw new ValidationException();
            }
            if (birthday.after(Date.from(Instant.now()))) {
                throw new ValidationException();
            }
            this.birthday = birthday;
            return this;
        }
    }
}
