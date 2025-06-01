package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    private final static Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Вызван метод findAll()");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User post) throws ValidationException {
        log.info("Создание нового пользователя: {}", post);
        if (post.getEmail() == null || post.getEmail().isBlank() || !post.getEmail().contains("@")) {
            throw new ValidationException("Email не может быть пустым и должен содержать символ '@'");
        }
        if (post.getLogin() == null || post.getLogin().isBlank() || post.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (post.getBirthday() == null || !post.getBirthday().isBefore(Instant.now())) {
            throw new ValidationException("Дата рождения не может быть пустой или в будущем");
        }
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
    public User update(@RequestBody User post) throws ValidationException {
        log.info("Обновление нового пользователя: {}", post.getId());
        if (post.getId() == null) {
            throw new ValidationException ("Id должен быть указан");
        }
        if (!users.containsKey(post.getId())) {
            throw new ValidationException("Фильм с таким ID не найден");
        }
        if (post.getEmail() == null || post.getEmail().isBlank() || !post.getEmail().contains("@")) {
            throw new ValidationException("Email не может быть пустым и должен содержать символ '@'");
        }
        if (post.getLogin() == null || post.getLogin().isBlank() || post.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (post.getBirthday() == null || !post.getBirthday().isBefore(Instant.now())) {
            throw new ValidationException("Дата рождения не может быть пустой или в будущем");
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
