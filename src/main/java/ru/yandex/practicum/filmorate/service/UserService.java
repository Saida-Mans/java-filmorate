package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    private final Set<String> emails = new HashSet<>();

    public void addFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        log.info("Adding friend relationship: user {} with friend {}", userId, friendId);
        user.addFriend(friendId);
        friend.addFriend(userId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);

        user.removeFriend(friendId);
        friend.removeFriend(userId);
    }

    public List<User> getFriends(Long userId) {
        User user = getUserOrThrow(userId);
        return user.getFriends().stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        User user = getUserOrThrow(userId);
        User otherUser = getUserOrThrow(otherUserId);

        Set<Long> commonIds = user.getFriends().stream()
                .filter(id -> otherUser.getFriends().contains(id))
                .collect(Collectors.toSet());

        return commonIds.stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        if (emails.contains(user.getEmail())) {
            throw new ValidationException("Email уже занят: " + user.getEmail());
        }
        User created = userStorage.add(user);
        emails.add(created.getEmail());
        return created;
    }

    public User update(User post) {
        if (post.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        User user = getUserOrThrow(post.getId());
        if (!post.getEmail().equals(user.getEmail())) {
            if (emails.contains(post.getEmail())) {
                throw new ValidationException("Email уже занят: " + post.getEmail());
            }
            emails.remove(user.getEmail());
            emails.add(post.getEmail());
        }
        if (post.getName() == null || post.getName().isBlank()) {
            post.setName(post.getLogin());
        }
        return userStorage.update(post);
    }

    public User getUserOrThrow(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            log.error("Пользователь с id {} не найден.", id);
            throw new UserNotFoundException("Пользователь с таким " + id + " не найден");
        }
        return user;
    }
}

