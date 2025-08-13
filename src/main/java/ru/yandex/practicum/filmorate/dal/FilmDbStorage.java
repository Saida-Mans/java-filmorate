package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.sql.Date;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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
            //film.setLikes(findLikesByFilmId(film.getId()));
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
        try {
            String sqlIds = """
            SELECT f.id
            FROM films f
            LEFT JOIN film_likes fl ON f.id = fl.film_id
            GROUP BY f.id
            ORDER BY COUNT(fl.user_id) DESC, f.id DESC
            LIMIT ?
        """;
            List<Long> topFilmIds = jdbc.query(sqlIds, (rs, rowNum) -> rs.getLong("id"), count);

            if (topFilmIds.isEmpty()) {
                return Collections.emptyList();
            }
            String inSql = topFilmIds.stream()
                    .map(id -> "?")
                    .collect(Collectors.joining(", ", "(", ")"));
            String sql = """
            SELECT
                f.id AS film_id,
                f.name,
                f.description,
                f.release_date,
                f.duration,
                f.rating_id,
                r.name AS rating_name,
                g.id AS genre_id,
                g.name AS genre_name
            FROM films f
            JOIN rating r ON f.rating_id = r.id
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.id
            WHERE f.id IN
        """ + inSql;
            return jdbc.query(con -> {
                var ps = con.prepareStatement(sql);
                for (int i = 0; i < topFilmIds.size(); i++) {
                    ps.setLong(i + 1, topFilmIds.get(i));
                }
                return ps;
            }, rs -> {
                Map<Long, Film> filmMap = new LinkedHashMap<>();
                while (rs.next()) {
                    long filmId = rs.getLong("film_id");
                    Film film = filmMap.computeIfAbsent(filmId, id -> {
                        try {
                            Film f = new Film();
                            f.setId(id);
                            f.setName(rs.getString("name"));
                            f.setDescription(rs.getString("description"));
                            f.setReleaseDate(rs.getDate("release_date").toLocalDate());
                            f.setDuration(rs.getLong("duration"));
                            f.setGenres(new HashSet<>());

                            Rating rating = new Rating();
                            rating.setId(rs.getLong("rating_id"));
                            rating.setName(rs.getString("rating_name"));
                            f.setRating(rating);
                            return f;
                        } catch (SQLException e) {
                            throw new RuntimeException("Ошибка при маппинге фильма с ID: " + id, e);
                        }
                    });

                    long genreId = rs.getLong("genre_id");
                    if (!rs.wasNull()) {
                        Genre genre = new Genre();
                        genre.setId(genreId);
                        genre.setName(rs.getString("genre_name"));
                        film.getGenres().add(genre);
                    }
                }
                return topFilmIds.stream()
                        .map(filmMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
            });
        } catch (Exception e) {
            log.error("Не удалось получить популярные фильмы", e);
            return Collections.emptyList();
        }
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
