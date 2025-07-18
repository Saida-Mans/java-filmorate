package ru.yandex.practicum.filmorate.dal;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Repository("filmDbStorage")
@Primary
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private final FilmGenreDbStorage filmGenreDbStorage;
    private final FilmLikeDbStorage filmLikeDbStorage;

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmGenreDbStorage filmGenreDbStorage, FilmLikeDbStorage filmLikeDbStorage) {
        super(jdbc, mapper);
        this.filmGenreDbStorage = filmGenreDbStorage;
        this.filmLikeDbStorage = filmLikeDbStorage;
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
        filmGenreDbStorage.deleteGenresByFilmId(id);
        filmGenreDbStorage.addGenresToFilm(id, film.getGenres());
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
        filmGenreDbStorage.deleteGenresByFilmId(film.getId());
        filmGenreDbStorage.addGenresToFilm(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film findById(Long id) {
        Film film = findOne(
                "SELECT f.*, r.name AS rating_name " + "FROM films f " + "JOIN rating r ON f.rating_id = r.id " + "WHERE f.id = ?", id).orElse(null);
        if (film != null) {
            film.setGenres(filmGenreDbStorage.findGenresByFilmId(film.getId()));
            film.setLikes(filmLikeDbStorage.findLikesByFilmId(film.getId()));
        }
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        Collection<Film> films = findMany("SELECT f.*, r.name AS rating_name " + "FROM films f " + "JOIN rating r ON f.rating_id = r.id");
        for (Film film : films) {
            film.setGenres(filmGenreDbStorage.findGenresByFilmId(film.getId()));
        }
        return films;
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
                    film.setGenres(filmGenreDbStorage.findGenresByFilmId(film.getId()));
                    film.setLikes(filmLikeDbStorage.findLikesByFilmId(film.getId()));
                })
                .collect(Collectors.toList());
    }
}
