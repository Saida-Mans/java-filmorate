package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film add(Film film);
    Film update(Film film);
    void delete(Long id);
    Film findById(Long id);
    Collection<Film> findAll();
}
