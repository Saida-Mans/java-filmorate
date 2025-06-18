package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        FilmService filmService = new FilmService(filmStorage, userStorage);
        filmController = new FilmController(filmService);
    }

    @Test
    void createFilm_Success() throws ValidationException {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120L);
        Film created = filmController.create(film);

        assertNotNull(created);
        assertEquals("Test Film", created.getName());
        assertTrue(created.getId() > 0);
        assertEquals(120, created.getDuration());
    }

    @Test
    void createFilm_ReleaseDateTooEarly_ThrowsException() {
        Film film = new Film();
        film.setName("Film");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(1800, 1, 1));
        film.setDuration(120L);

        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.create(film));
        assertEquals("Дата релиза — не раньше 28 декабря 1895 года", ex.getMessage());
    }

    @Test
    void updateFilm_Success() throws ValidationException {
        Film film = new Film();
        film.setName("Original Film");
        film.setDescription("Описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(100L);
        Film created = filmController.create(film);

        Film update = new Film();
        update.setId(created.getId());
        update.setName("Updated Film");
        update.setDescription("Новое описание");
        update.setReleaseDate(LocalDate.of(2000, 1, 1));
        update.setDuration(150L);

        Film updated = filmController.update(update);

        assertEquals("Updated Film", updated.getName());
        assertEquals("Новое описание", updated.getDescription());
        assertEquals(150, updated.getDuration());
    }

    @Test
    void updateFilm_IdNull_ThrowsException() {
        Film film = new Film();
        film.setId(null);
        ValidationException ex = assertThrows(ValidationException.class, () -> filmController.update(film));
        assertEquals("Id должен быть указан", ex.getMessage());
    }

    @Test
    void updateFilm_IdNotFound_ThrowsException() {
        Film film = new Film();
        film.setId(999L);
        FilmNotFoundException ex = assertThrows(FilmNotFoundException.class, () -> filmController.update(film));
        assertEquals("Фильм с таким ID не найден", ex.getMessage());
    }
}
