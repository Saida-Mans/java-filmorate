package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.dal.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.RatingDbStorage;
import ru.yandex.practicum.filmorate.dal.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dal.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    private final FilmGenreDbStorage filmGenreDbStorage;

    private final FilmLikeDbStorage filmLikeDbStorage;

    private final RatingDbStorage ratingDbStorage;

    private final GenreDbStorage genreDbStorage;

    public FilmService(
            @Qualifier("filmDbStorage") FilmStorage filmStorage,
            @Qualifier("userDbStorage") UserStorage userStorage,
            FilmGenreDbStorage filmGenreDbStorage,
            FilmLikeDbStorage filmLikeDbStorage,
            RatingDbStorage ratingDbStorage, GenreDbStorage genreDbStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmGenreDbStorage = filmGenreDbStorage;
        this.filmLikeDbStorage = filmLikeDbStorage;
        this.ratingDbStorage = ratingDbStorage;
        this.genreDbStorage = genreDbStorage;
    }

    public void addLike(Long filmId, Long userId) {
       getFilmOrThrow(filmId);
       getUserOrThrow(userId);
        filmLikeDbStorage.addLike(filmId, userId);
    }

    public void removeLike(Long filmId, Long userId) {
        getFilmOrThrow(filmId);
        getUserOrThrow(userId);
        filmLikeDbStorage.removeLike(filmId, userId);
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparingInt(f -> -f.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Collection<Film> findAll() {
        Collection<Film> films = filmStorage.findAll();
        for (Film film : films) {
            film.setGenres(filmGenreDbStorage.findGenresByFilmId(film.getId()));
        }
        return films;
    }

    public Film create(NewFilmRequest request) {
        if (request.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        Rating rating = ratingDbStorage.findById(request.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + request.getMpa().getId() + " не найден"));
        Film film = FilmMapper.mapToFilm(request, rating);
        validateFilm(film);
        Set<Genre> genres = film.getGenres();
        if (genres != null) {
            List<Genre> validatedGenres = new ArrayList<>();
            for (Genre genre : genres) {
                Genre found = genreDbStorage.findById(genre.getId())
                        .orElseThrow(() -> new NotFoundException("Жанр с id = " + genre.getId() + " не найден"));
                validatedGenres.add(found);
            }
            film.setGenres(new HashSet<>(validatedGenres));
        }
        Set<Genre> uniqueGenres = FilmMapper.deduplicateGenres(film.getGenres());
        film.setGenres(uniqueGenres);

        Film saved = filmStorage.add(film);
        filmGenreDbStorage.deleteGenresByFilmId(saved.getId());
        filmGenreDbStorage.addGenresToFilm(saved.getId(), uniqueGenres);
        saved.setGenres(filmGenreDbStorage.findGenresByFilmId(saved.getId()));
        return saved;
    }

    public Film update(UpdateFilmRequest request) {
        Film film = getFilmOrThrow(request.getId());

        Rating rating;
        if (request.getRatingId() != null) {
            rating = ratingDbStorage.findById(request.getRatingId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг с id = " + request.getRatingId() + " не найден"));
        } else {
            rating = film.getRating();
        }
        Film updated = FilmMapper.updateFilmFields(film, request, rating);
        validateFilm(updated);
        Film saved = filmStorage.update(updated);
        filmGenreDbStorage.deleteGenresByFilmId(saved.getId());
        if (updated.getGenres() != null && !updated.getGenres().isEmpty()) {
            filmGenreDbStorage.addGenresToFilm(saved.getId(), updated.getGenres());
        }
        saved.setGenres(filmGenreDbStorage.findGenresByFilmId(saved.getId()));
        return saved;
    }

    private void validateFilm(Film film) {
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
            throw new UserNotFoundException("Пользователь с таким ID не найден");
        }
        return user;
    }

    private Film getFilmOrThrow(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new FilmNotFoundException("Фильм с id " + id + " не найден");
        }
        film.setGenres(filmGenreDbStorage.findGenresByFilmId(id));
        return film;
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.findTopFilms(count);
    }

    public Film getById(Long id) {
        Film film = filmStorage.findById(id);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + id + " не найден");
        }
        return film;
    }
}
