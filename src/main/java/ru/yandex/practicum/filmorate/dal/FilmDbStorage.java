package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film add(Film film) {
        long id = insert(
                "INSERT INTO films (name, description, release_date, duration, rating_id) " +
                        "VALUES (?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getRating().getId());
        film.setId(id);
        deleteGenresByFilmId(id);
        addGenresToFilm(id, film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        update(
                "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? WHERE id = ?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getRating().getId(),
                film.getId());
        deleteGenresByFilmId(film.getId());
        addGenresToFilm(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film findById(Long id) {
        Film film = findOne(
                "SELECT f.*, r.name AS rating_name " + "FROM films f " + "JOIN rating r ON f.rating_id = r.id " + "WHERE f.id = ?", id).orElse(null);
        if (film != null) {
            film.setGenres(findGenresByFilmId(film.getId()));
            film.setLikes(findLikesByFilmId(film.getId()));
        }
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return findMany("""
        SELECT f.*, r.name AS rating_name
        FROM films f
        JOIN rating r ON f.rating_id = r.id
    """);
    }

    public List<Film> findTopFilms(int count) {
        String sql = """
        SELECT f.*, r.name AS rating_name
        FROM films f
        JOIN rating r ON f.rating_id = r.id
        LEFT JOIN film_likes fl ON f.id = fl.film_id
        GROUP BY f.id, r.name
        ORDER BY COUNT(fl.user_id) DESC
        LIMIT ?
    """;
        return jdbc.query(sql, new FilmRowMapper(), count)
                .stream()
                .peek(film -> {
                    film.setGenres(findGenresByFilmId(film.getId()));
                    film.setLikes(findLikesByFilmId(film.getId()));
                })
                .collect(Collectors.toList());
    }

    private void addGenresToFilm(Long filmId, Collection<Genre> genres) {
        String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
        for (Genre genre : genres) {
            jdbc.update(sql, filmId, genre.getId());
        }
    }

    private void deleteGenresByFilmId(Long filmId) {
        jdbc.update("DELETE FROM film_genres WHERE film_id = ?", filmId);
    }

    private Set<Genre> findGenresByFilmId(Long filmId) {
        String sql = """
        SELECT g.id, g.name
        FROM genres g
        JOIN film_genres fg ON g.id = fg.genre_id
        WHERE fg.film_id = ?
        ORDER BY g.id
    """;
        return new LinkedHashSet<>(jdbc.query(sql,
                (rs, rowNum) -> new Genre(rs.getLong("id"), rs.getString("name")),
                filmId));
    }

    private Set<Long> findLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbc.query(sql,
                (rs, rowNum) -> rs.getLong("user_id"),
                filmId));
    }
}
