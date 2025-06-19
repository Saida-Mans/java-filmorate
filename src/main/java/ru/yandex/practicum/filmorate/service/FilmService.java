package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);


    public void addLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);
        film.addLike(user.getId());
    }

    public void removeLike(Long filmId, Long userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);
        film.removeLike(userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film create(Film post) {
        validateFilm(post);
        return filmStorage.add(post);
    }

    public Film update(Film post) {
        validateFilm(post);
        if (filmStorage.findById(post.getId()) == null) {
            throw new FilmNotFoundException("Фильм с id " + post.getId() + " не найден");
        }
        return filmStorage.update(post);
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    private User getUserOrThrow(Long id) {
        User user = userStorage.findById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с таким " + id + " не найден");
        }
        return user;
    }

    private Film getFilmOrThrow(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
        return film;
    }
}
