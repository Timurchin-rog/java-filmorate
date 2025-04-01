package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.UserDB;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;


public final class UserMapper {
    public static UserDB mapToUserDB(User user) {
        return UserDB.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
                .build();
    }

    public static UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .login(user.getLogin())
                .name(user.getName())
                .birthday(user.getBirthday())
                .friends(user.getFriends())
                .build();
    }

    public static UserDB updateUserFields(UserDB user, User userFromRequest) {
        if (userFromRequest.hasEmail()) {
            user.setEmail(userFromRequest.getEmail());
        }
        if (userFromRequest.hasLogin()) {
            user.setLogin(userFromRequest.getLogin());
        }
        if (userFromRequest.hasName()) {
            user.setName(userFromRequest.getName());
        }
        if (userFromRequest.hasBirthday()) {
            user.setBirthday(userFromRequest.getBirthday());
        }
        return user;
    }
}

