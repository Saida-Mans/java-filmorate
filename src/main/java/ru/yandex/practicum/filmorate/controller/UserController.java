package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.OnCreate;
import ru.yandex.practicum.filmorate.model.OnUpdate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import lombok.extern.slf4j.Slf4j;
import java.util.Collection;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {


    private final UserService userService;

    @GetMapping
    public Collection<User> findAll() {
        return userService.findAll();
    }

    @PostMapping
    public User create(@Validated(OnCreate.class) @RequestBody User post) throws ValidationException {
        return userService.create(post);
    }

    @PutMapping
    public User update(@Validated(OnUpdate.class) @RequestBody User post) throws UserNotFoundException {
        return userService.update(post);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable @Positive(message = "id должен быть положительным") long id, @PathVariable @Positive(message = "friendId должен быть положительным") long friendId) {
         userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable @Positive(message = "id должен быть положительным") long id, @PathVariable @Positive(message = "friendId должен быть положительным") long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable @Positive(message = "id должен быть положительным") long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public  List<User> getCommonFriends(@PathVariable @Positive(message = "id должен быть положительным") long id, @PathVariable @Positive(message = "otherId должен быть положительным") long otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}

