package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private final Map<Long, User> users = new HashMap<>();

    public User add(User post) {
        if (post.getName() == null || post.getName().isBlank()) {
            post.setName(post.getLogin());
        }
        post.setId(getNextId());
        users.put(post.getId(), post);
        return post;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public User update(User post) {
        if (post.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (!users.containsKey(post.getId())) {
            throw new UserNotFoundException("Пользователь с таким ID не найден");
        }
        if (post.getName() == null || post.getName().isBlank()) {
            post.setName(post.getLogin());
        }
        User oldPost = users.get(post.getId());
        oldPost.setEmail(post.getEmail());
        oldPost.setLogin(post.getLogin());
        oldPost.setName(post.getName());
        oldPost.setBirthday(post.getBirthday());
        return oldPost;
    }

    public User findById(Long id) {
        if (id == null || id <= 0) {
            throw new ValidationException("Id пользователя должен быть положительным числом.");
        }
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        return user;
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public void delete(Long id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден.");
        }
        users.remove(id);
    }
}