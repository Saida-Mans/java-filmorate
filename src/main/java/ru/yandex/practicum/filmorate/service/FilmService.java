package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {

        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        if (userId == null || userId <= 0) {
            throw new ValidationException("Id пользователя должен быть положительным числом.");
        }
        userStorage.findById(userId);
        film.addLike(userId);
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = filmStorage.findById(filmId);
        if (userId == null || userId <= 0) {
            throw new ValidationException("Id пользователя должен быть положительным числом.");
        }
        userStorage.findById(userId);
        film.removeLike(userId);
    }

    public List<Film> getTopFilms(int count) {
        if (count <= 0) {
            throw new ValidationException("Параметр count должен быть положительным числом.");
        }
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film post) {
        return filmStorage.add(post);
    }

    public Film update(Film post) {
        return filmStorage.update(post);
    }
}
