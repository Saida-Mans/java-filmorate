package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    public Film add(Film post) {
        post.setId(getNextId());
        films.put(post.getId(), post);
        return post;
    }

    public Film update(Film post) {
        Film oldFilm = films.get(post.getId());
        oldFilm.setName(post.getName());
        oldFilm.setDescription(post.getDescription());
        oldFilm.setReleaseDate(post.getReleaseDate());
        oldFilm.setDuration(post.getDuration());
        return oldFilm;
    }

    public Film findById(Long id) {
        Film film = films.get(id);
        return film;
    }

    public Collection<Film> findAll() {
        return films.values();
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}



