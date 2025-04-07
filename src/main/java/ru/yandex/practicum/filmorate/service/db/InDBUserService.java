package ru.yandex.practicum.filmorate.service.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dto.UserDB;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InDBUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.getAllUsers().stream()
                .map(this::mapToUser)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(int userId) {
        if (userRepository.getUserById(userId).isEmpty())
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        UserDB userDB = userRepository.getUserById(userId).get();
        userDB.setFriends(userRepository.getAllFriendOfUser(userId));
        User user = mapToUser(userDB);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto create(User userFromRequest) {
        if (checkDuplicatedEmail(userFromRequest)) {
            throw new DuplicatedDataException("Имейл уже используется");
        }
        User newUser = userFromRequest.toBuilder().friends(new HashSet<>()).build();
        UserDB userDB = UserMapper.mapToUserDB(newUser);
        userRepository.saveUser(userDB);
        User userForClients = mapToUser(userDB);
        return UserMapper.mapToUserDto(userForClients);
    }

    @Override
    public UserDto update(User userFromRequest) {
        if (userFromRequest.getId() == null) {
            throw new ValidationException("При обновлении пользователя не указан id");
        }
        int userId = userFromRequest.getId();
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
        if (checkDuplicatedEmail(userFromRequest)) {
            throw new DuplicatedDataException("Данный имейл уже используется");
        }
        UserDB oldUser = userRepository.getUserById(userId).get();
        oldUser.setFriends(userRepository.getAllFriendOfUser(userId));
        UserDB updatedOldUser = UserMapper.updateUserFields(oldUser, userFromRequest);
        User user = mapToUser(updatedOldUser);
        UserDB userDB = UserMapper.mapToUserDB(user);
        userRepository.updateUser(userDB);
        log.info(String.format("Ползователь id = %d обновлён", userId));
        return UserMapper.mapToUserDto(user);
    }

    private boolean checkDuplicatedEmail(User user) {
        return userRepository.getAllUsers().stream()
                .filter(userDb -> !Objects.equals(userDb.getId(), user.getId()))
                .anyMatch(userDB -> userDB.getEmail().equals(user.getEmail()));
    }

    @Override
    public String remove(int userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            return String.format("Пользователь %d не существует", userId);
        }
        userRepository.removeUser(userId);
        return String.format("Пользователь id = %d удалён", userId);
    }

    private User mapToUser(UserDB userDB) {
        return User.builder()
                .id(userDB.getId())
                .email(userDB.getEmail())
                .login(userDB.getLogin())
                .name(userDB.getName())
                .birthday(userDB.getBirthday())
                .friends(userRepository.getAllFriendOfUser(userDB.getId()))
                .build();
    }

    @Override
    public void addFriend(int userId, int friendId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
        UserDB user = userRepository.getUserById(userId).get();
        if (userRepository.getUserById(friendId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", friendId));
        }
        user.setFriends(userRepository.getAllFriendOfUser(userId));
        userRepository.addFriend(userId, friendId);
        user.getFriends().add(friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
        UserDB user = userRepository.getUserById(userId).get();
        if (userRepository.getUserById(friendId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", friendId));
        }
        user.setFriends(userRepository.getAllFriendOfUser(userId));
        userRepository.removeFriend(userId, friendId);
        user.getFriends().remove(friendId);
    }

    @Override
    public List<UserDto> findAllFriends(int userId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
        UserDB user = userRepository.getUserById(userId).get();
        user.setFriends(userRepository.getAllFriendOfUser(userId));
        return user.getFriends().stream()
                .map(friendId -> {
                    if (userRepository.getUserById(friendId).isEmpty()) {
                        throw new NotFoundException(String.format("Пользователь id = %d не найден", friendId));
                    }
                    UserDB friend = userRepository.getUserById(friendId).get();
                    friend.setFriends(userRepository.getAllFriendOfUser(friendId));
                    return friend;
                })
                .map(this::mapToUser)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findCommonFriends(int userId, int otherUserId) {
        if (userRepository.getUserById(userId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", userId));
        }
        UserDB user = userRepository.getUserById(userId).get();
        user.setFriends(userRepository.getAllFriendOfUser(userId));
        if (userRepository.getUserById(otherUserId).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь id = %d не найден", otherUserId));
        }
        UserDB otherUser = userRepository.getUserById(otherUserId).get();
        otherUser.setFriends(userRepository.getAllFriendOfUser(otherUserId));
        return user.getFriends().stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(friendId -> {
                    if (userRepository.getUserById(friendId).isEmpty()) {
                        throw new NotFoundException(String.format("Пользователь id = %d не найден", friendId));
                    }
                    UserDB friend = userRepository.getUserById(friendId).get();
                    friend.setFriends(userRepository.getAllFriendOfUser(friendId));
                    return friend;
                })
                .map(this::mapToUser)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}
