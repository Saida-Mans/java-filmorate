package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        validateId(userId);
        validateId(friendId);

        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        validateId(userId);
        validateId(friendId);

        User user = userStorage.findById(userId);
        User friend = userStorage.findById(friendId);

        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public List<User> getFriends(Long userId) {
        validateId(userId);

        User user = userStorage.findById(userId);
        return user.getFriends().stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        validateId(userId);
        validateId(otherUserId);

        User user = userStorage.findById(userId);
        User otherUser = userStorage.findById(otherUserId);

        Set<Long> commonIds = user.getFriends().stream()
                .filter(id -> otherUser.getFriends().contains(id))
                .collect(Collectors.toSet());

        return commonIds.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        return userStorage.add(user);
    }

    public User update(User post) {
        return userStorage.update(post);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id пользователя должен быть положительным числом.");
        }
    }
}
