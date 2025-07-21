package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dal.dto.FilmDto;
import ru.yandex.practicum.filmorate.dal.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dal.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;
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

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @PostMapping
    public ResponseEntity<FilmDto> create(@RequestBody @Valid NewFilmRequest request) {
        Film film = filmService.create(request);
        return ResponseEntity.ok(FilmMapper.mapToFilmDto(film, film.getRating()));
    }

    @PutMapping
    public ResponseEntity<FilmDto> update(@Valid @RequestBody UpdateFilmRequest post) throws ValidationException {
            if (post.getReleaseDate() != null &&
                    post.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        Film film = filmService.update(post);
        return ResponseEntity.ok(FilmMapper.mapToFilmDto(film, film.getRating()));
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
    public List<FilmDto> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FilmDto> getFilmById(@PathVariable Long id) {
        Film film = filmService.getById(id);
        Rating rating = film.getRating();
        return ResponseEntity.ok(FilmMapper.mapToFilmDto(film, rating));
    }
}