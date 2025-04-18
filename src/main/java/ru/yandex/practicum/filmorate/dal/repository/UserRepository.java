package ru.yandex.practicum.filmorate.dal.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.dto.UserDB;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserRepository extends BaseRepository<UserDB> {
    private final ReviewRepository reviewRepository;
    private final FeedRepository feedRepository;

    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_USER = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String UPDATE_USER = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";
    private static final String INSERT_FRIEND_OF_USER = "INSERT INTO users_friends(user_id, friend_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_USER_FROM_FRIENDS_LIST = "DELETE FROM users_friends WHERE user_id = ? OR friend_id = ?";
    private static final String DELETE_FRIEND_OF_USER = "DELETE FROM users_friends WHERE user_id = ? AND friend_id = ?";
    private static final String FIND_ALL_FRIENDS_OF_USER = "SELECT friend_id FROM users_friends WHERE user_id = ?";

    public UserRepository(JdbcTemplate jdbc,
                          RowMapper<UserDB> mapper,
                          ReviewRepository reviewRepository,
                          FeedRepository feedRepository) {
        super(jdbc, mapper);
        this.reviewRepository = reviewRepository;
        this.feedRepository = feedRepository;
    }

    public List<UserDB> getAllUsers() {
        List<UserDB> userDBList = findMany(FIND_ALL_USERS);
        return userDBList.stream()
                .peek(userDB -> userDB.setFriends(getAllFriendOfUser(userDB.getId())))
                .sorted(Comparator.comparing(UserDB::getId))
                .collect(Collectors.toList());
    }

    public UserDB getUserById(int userId) {
        Optional<UserDB> userOpt = findOne(FIND_USER_BY_ID, userId);
        if (userOpt.isEmpty())
            throw new NotFoundException();
        userOpt.get().setFriends(getAllFriendOfUser(userId));
        return userOpt.get();
    }

    public void saveUser(UserDB user) {
        int id = insert(
                INSERT_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
    }

    public void updateUser(UserDB user) {
        update(
                UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
    }

    public void removeUser(int userId) {
        removeUserFromFriendsList(getUserById(userId));
        reviewRepository.removeReviewsOfUser(userId);
        feedRepository.removeAllFeedsOfUser(userId);
        delete(DELETE_USER, userId);
    }

    public void addFriend(int userId, int friendId) {
        insert(
                INSERT_FRIEND_OF_USER,
                userId,
                friendId
        );
    }

    private void removeUserFromFriendsList(UserDB userDB) {
        if (userDB.getFriends().isEmpty())
            return;
        delete(DELETE_USER_FROM_FRIENDS_LIST, userDB.getId(), userDB.getId());
    }

    public void removeFriend(int userId, int friendId) {
        delete(DELETE_FRIEND_OF_USER, userId, friendId);
    }

    public Set<Integer> getAllFriendOfUser(int userId) {
        return findManyId(FIND_ALL_FRIENDS_OF_USER, userId);
    }
}