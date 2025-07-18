package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage {
    private final JdbcTemplate jdbc;

    public void deleteGenresByFilmId(Long filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = ?";
        jdbc.update(sql, filmId);
    }

    public void addGenresToFilm(Long filmId, Set<Genre> genres) {
        if (genres == null || genres.isEmpty()) return;
        Set<Long> seenIds = new HashSet<>();
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            if (genre != null && genre.getId() != null && seenIds.add(genre.getId())) {
                jdbc.update(sql, filmId, genre.getId());
            }
        }
    }

    public Set<Genre> findGenresByFilmId(Long filmId) {
        String sql = "SELECT g.id, g.name FROM genres g " +
                "JOIN film_genres fg ON g.id = fg.genre_id " +
                "WHERE fg.film_id = ?";
        List<Genre> genres = jdbc.query(sql, new GenreMapper(), filmId);
        return new HashSet<>(genres);
    }
    }
