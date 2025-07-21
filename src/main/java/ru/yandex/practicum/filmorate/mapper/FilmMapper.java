package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dal.dto.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.*;
import java.util.stream.Collectors;

public class FilmMapper {
public static Film mapToFilm(NewFilmRequest request, Rating rating) {
    Film film = new Film();
    film.setName(request.getName());
    film.setDescription(request.getDescription());
    film.setReleaseDate(request.getReleaseDate());
    film.setDuration(request.getDuration());
    film.setGenres(request.getGenres());
    film.setRating(rating);
    return film;
}

public static FilmDto mapToFilmDto(Film film, Rating rating) {
    FilmDto dto = new FilmDto();
    dto.setId(film.getId());
    dto.setName(film.getName());
    dto.setDescription(film.getDescription());
    dto.setReleaseDate(film.getReleaseDate());
    dto.setDuration(film.getDuration());
    dto.setDuration(film.getDuration());
    List<Genre> sortedGenres = film.getGenres().stream()
            .sorted(Comparator.comparing(Genre::getId))
            .collect(Collectors.toList());

    dto.setGenres(sortedGenres);
    dto.setMpa(rating);
    return dto;
}

public static Film updateFilmFields(Film film, UpdateFilmRequest request, Rating rating) {
    if (request.hasName()) {
        film.setName(request.getName());
    }
    if (request.hasDescription()) {
        film.setDescription(request.getDescription());
    }
    if (request.hasReleaseDate()) {
        film.setReleaseDate(request.getReleaseDate());
    }
    if (request.hasDuration()) {
        film.setDuration(request.getDuration());
    }
    if (request.hasGenres()) {
        film.setGenres(request.getGenres());
    }
    if (request.hasRating()) {
        film.setRating(rating);
    }
    return film;
}

    public static Set<Genre> deduplicateGenres(Set<Genre> genres) {
        if (genres == null) return new HashSet<>();
        return genres.stream()
                .filter(g -> g != null && g.getId() != null)
                .collect(Collectors.toMap(
                        Genre::getId,
                        g -> g,
                        (g1, g2) -> g1
                ))
                .values()
                .stream()
                .collect(Collectors.toSet());
    }
}