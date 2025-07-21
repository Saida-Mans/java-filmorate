package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.dal.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dal.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipDbStorage friendshipDbStorage;

    public UserService(UserStorage userStorage, FriendshipDbStorage friendshipDbStorage) {
        this.userStorage = userStorage;
        this.friendshipDbStorage = friendshipDbStorage;
    }

    public void addFriend(Long userId, Long friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        log.info("Adding friend relationship: user {} with friend {}", userId, friendId);
        friendshipDbStorage.addFriend(userId, friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        getUserOrThrow(userId);
        getUserOrThrow(friendId);
        friendshipDbStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(Long userId) {
       getUserOrThrow(userId);
        List<Friendship> friendships = friendshipDbStorage.findFriendsOfUser(userId);
        return friendships.stream()
                .map(Friendship::getFriendId)
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        getUserOrThrow(userId);
        getUserOrThrow(otherUserId);
        Set<Long> userFriendIds = friendshipDbStorage.findFriendsOfUser(userId).stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        Set<Long> otherFriendIds = friendshipDbStorage.findFriendsOfUser(otherUserId).stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toSet());

        Set<Long> commonIds = userFriendIds.stream()
                .filter(otherFriendIds::contains)
                .collect(Collectors.toSet());

        return commonIds.stream()
                .map(this::getUserOrThrow)
                .collect(Collectors.toList());
    }

    public List<UserDto> getUsers() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto create(NewUserRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ValidationException("Емейл должен быть указан");
        }

        if (userStorage.containsEmail(request.getEmail())) {
            throw new ValidationException("Данный имейл уже используется");
        }

        User user = UserMapper.mapToUser(request);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user = userStorage.add(user);

        return UserMapper.mapToUserDto(user);
    }

    public UserDto update(UpdateUserRequest request) {
        if (request == null) {
            throw new ValidationException("Тело запроса не должно быть пустым");
        }
        if (!request.hasId()) {
            throw new ValidationException("ID обязателен для обновления пользователя");
        }
        User existing = getUserOrThrow(request.getId());

        if (request.hasEmail()) existing.setEmail(request.getEmail());
        if (request.hasLogin()) existing.setLogin(request.getLogin());
        if (request.hasBirthday()) existing.setBirthday(request.getBirthday());

        if (request.hasName()) {
            if (request.getName().isBlank()) {
                existing.setName(existing.getLogin());
            } else {
                existing.setName(request.getName());
            }
        }
        return UserMapper.mapToUserDto(userStorage.update(existing));
    }

    public User getUserOrThrow(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            log.error("Пользователь с id {} не найден.", id);
            throw new UserNotFoundException("Пользователь с таким ID не найден");
        }
        return user;
    }
}

