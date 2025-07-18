package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dal.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dal.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dal.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.OnCreate;
import ru.yandex.practicum.filmorate.service.UserService;
import lombok.extern.slf4j.Slf4j;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public Collection<UserDto> findAll() {
        return userService.getUsers();
    }

    @PostMapping
    public UserDto create(@Validated(OnCreate.class) @RequestBody NewUserRequest request) throws ValidationException {
        return userService.create(request);
    }

    @PutMapping
    public UserDto update(@RequestBody UpdateUserRequest request) throws UserNotFoundException {
        return userService.update(request);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable @Positive long id, @PathVariable @Positive long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable @Positive(message = "id должен быть положительным") long id, @PathVariable @Positive(message = "friendId должен быть положительным") long friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<UserDto> getFriends(@PathVariable @Positive(message = "id должен быть положительным") long id) {
        return userService.getFriends(id).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getCommonFriends(@PathVariable @Positive long id,
                                          @PathVariable @Positive long otherId) {
        return userService.getCommonFriends(id, otherId).stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }
}

