package ru.yandex.practicum.filmorate.storage;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    Film findById(Long id);

    Collection<Film> findAll();

    List<Film> findTopFilms(int count);
}
