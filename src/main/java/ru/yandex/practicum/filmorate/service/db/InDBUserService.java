package ru.yandex.practicum.filmorate.service.db;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repository.FeedRepository;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.dto.UserDB;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.DuplicatedEmailException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;
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
    private final FeedRepository feedRepository;

    @Override
    public List<UserDto> findAll() {
        return userRepository.getAllUsers().stream()
                .map(this::mapToUser)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(int userId) {
        UserDB userDB = userRepository.getUserById(userId);
        User user = mapToUser(userDB);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    public UserDto create(User userFromRequest) {
        if (checkDuplicatedEmail(userFromRequest)) {
            throw new DuplicatedEmailException();
        }
        if (userFromRequest.getName() == null || userFromRequest.getName().isBlank()) {
            userFromRequest = userFromRequest.toBuilder()
                    .name(userFromRequest.getLogin())
                    .build();
        }
        User newUser = userFromRequest.toBuilder()
                .friends(new HashSet<>())
                .build();

        UserDB userDB = UserMapper.mapToUserDB(newUser);
        userRepository.saveUser(userDB);
        User userForClients = mapToUser(userDB);
        return UserMapper.mapToUserDto(userForClients);
    }

    @Override
    public UserDto update(User userFromRequest) {
        if (userFromRequest.getId() == null) {
            throw new ValidationException();
        }
        int userId = userFromRequest.getId();
        userRepository.getUserById(userId);
        if (checkDuplicatedEmail(userFromRequest)) {
            throw new DuplicatedEmailException();
        }
        UserDB oldUser = userRepository.getUserById(userId);
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
    public void remove(int userId) {
        userRepository.getUserById(userId);
        userRepository.removeUser(userId);
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

    @Transactional
    @Override
    public void addFriend(int userId, int friendId) {
        UserDB user = userRepository.getUserById(userId);
        userRepository.getUserById(friendId);
        userRepository.addFriend(userId, friendId);
        feedRepository.save(FeedEvent.builder()
                .actorUserId(userId)
                .affectedUserId(userId)
                .eventType("FRIEND")
                .operation("ADD")
                .entityId((long) friendId)
                .build());
        user.getFriends().add(friendId);
    }

    @Transactional
    @Override
    public void removeFriend(int userId, int friendId) {
        UserDB user = userRepository.getUserById(userId);
        userRepository.getUserById(friendId);
        userRepository.removeFriend(userId, friendId);
        feedRepository.save(FeedEvent.builder()
                .actorUserId(userId)
                .affectedUserId(userId)
                .eventType("FRIEND")
                .operation("REMOVE")
                .entityId((long) friendId)
                .build());
        user.getFriends().remove(friendId);
    }

    @Override
    public List<UserDto> findAllFriends(int userId) {
        UserDB user = userRepository.getUserById(userId);
        return user.getFriends().stream()
                .map(userRepository::getUserById)
                .map(this::mapToUser)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDto> findCommonFriends(int userId, int otherUserId) {
        UserDB user = userRepository.getUserById(userId);
        UserDB otherUser = userRepository.getUserById(otherUserId);
        return user.getFriends().stream()
                .filter(friendId -> otherUser.getFriends().contains(friendId))
                .map(userRepository::getUserById)
                .map(this::mapToUser)
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}
