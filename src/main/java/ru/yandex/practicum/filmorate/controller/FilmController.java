package ru.yandex.practicum.filmorate.controller;

import ch.qos.logback.classic.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/film")
public class FilmController {

    private final static Logger log = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Вызван метод findAll()");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film post) throws ValidationException {
        log.info("Создание нового фильма: {}", post);
        if (post.getName() == null || post.getName().isBlank()) {
            throw new ValidationException ("Название не может быть пустым");
        }
        if(post.getDescription().length()>200) {
            throw new ValidationException ("Mаксимальная длина описания — 200 символов");
        }
        if (post.getReleaseDate().isBefore(MIN_RELEASE_DATE.atStartOfDay(ZoneOffset.UTC).toInstant())) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (post.getDuration()<=0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        post.setId(getNextId());
        post.setReleaseDate(Instant.now());
        films.put(post.getId(), post);
        log.info("Фильм успешно создан с id={}", post.getId());
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

    @PutMapping
    public Film update(@RequestBody Film post) throws ValidationException {
        log.info("Обновление фильма с id={}", post.getId());
        if (post.getId() == null) {
            throw new ValidationException ("Id должен быть указан");
        }
        if (!films.containsKey(post.getId())) {
            throw new ValidationException("Фильм с таким ID не найден");
        }
        if (post.getName() == null || post.getName().isBlank()) {
            throw new ValidationException ("Название не может быть пустым");
        }
        if(post.getDescription().length()>200) {
            throw new ValidationException ("Mаксимальная длина описания — 200 символов");
        }
        if (post.getReleaseDate().isBefore(MIN_RELEASE_DATE.atStartOfDay(ZoneOffset.UTC).toInstant())) {
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (post.getDuration()<=0) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        Film oldFilm = films.get(post.getId());

        oldFilm.setName(post.getName());
        oldFilm.setDescription(post.getDescription());
        oldFilm.setReleaseDate(post.getReleaseDate());
        oldFilm.setDuration(post.getDuration());
        log.info("Фильм с id={} успешно обновлён", post.getId());
        return oldFilm;
    }
}
