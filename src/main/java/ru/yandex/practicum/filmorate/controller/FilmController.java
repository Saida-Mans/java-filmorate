package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film post) throws ValidationException  {
        if (post.getReleaseDate() != null &&
                post.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        return filmService.create(post);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film post) throws ValidationException {
            if (post.getReleaseDate() != null &&
                    post.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        return filmService.update(post);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable @Positive(message = "id должен быть положительным") long id, @PathVariable @Positive(message = "userId должен быть положительным") long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable @Positive(message = "id должен быть положительным") long id, @PathVariable @Positive(message = "userId должен быть положительным") long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopFilms(@RequestParam (defaultValue = "10") @Min(value = 1, message = "Параметр count должен быть не меньше 1") Integer count) {
        return filmService.getTopFilms(count);
    }
}
