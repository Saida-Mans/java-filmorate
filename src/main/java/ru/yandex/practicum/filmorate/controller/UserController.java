package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.OnCreate;
import ru.yandex.practicum.filmorate.model.OnUpdate;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Вызван метод findAll()");
        return users.values();
    }

    @PostMapping
    public User create(@Validated(OnCreate.class) @RequestBody User post) throws ValidationException {
        log.info("Создание нового пользователя: {}", post);
        if (post.getName() == null || post.getName().isBlank()) {
            post.setName(post.getLogin());
        }
        post.setId(getNextId());
        users.put(post.getId(), post);
        log.info("Пользователь успешно создан с id={}", post.getId());
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

    @PutMapping
    public User update(@Validated(OnUpdate.class) @RequestBody User post) throws UserNotFoundException {
        log.info("Обновление пользователя с id: {}", post.getId());
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
        log.info("Пользователь с id={} успешно обновлён", post.getId());
        return oldPost;
    }
}

