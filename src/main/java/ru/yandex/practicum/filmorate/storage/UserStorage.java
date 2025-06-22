package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User add(User user);

    User update(User user);

    User findById(Long id);

    Collection<User> findAll();

    boolean containsEmail(String email);

    void addEmail(String email);

    void removeEmail(String email);
}