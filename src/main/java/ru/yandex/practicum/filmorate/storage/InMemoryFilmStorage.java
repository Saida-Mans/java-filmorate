package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    public Film add(Film post) {
        if (post.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        post.setId(getNextId());
        films.put(post.getId(), post);
        return post;
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public Film update(Film post) {
        if (post.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }
        if (!films.containsKey(post.getId())) {
            throw new FilmNotFoundException("Фильм с таким ID не найден");
        }
        if (post.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }

        Film oldFilm = films.get(post.getId());
        oldFilm.setName(post.getName());
        oldFilm.setDescription(post.getDescription());
        oldFilm.setReleaseDate(post.getReleaseDate());
        oldFilm.setDuration(post.getDuration());
        return oldFilm;
    }

    public Film findById(Long id) {
        Film film = films.get(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден.");
        }
        return film;
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    public void delete(Long id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден.");
        }
        films.remove(id);
    }
}



