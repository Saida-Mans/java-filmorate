package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreDbStorage {

    private final JdbcTemplate jdbc;

    public GenreDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Genre> findAll() {
        String sql = "SELECT id, name FROM genres ORDER BY id";
        return jdbc.query(sql, new GenreMapper());
    }

    public Optional<Genre> findById(Long id) {
        String sql = "SELECT id, name FROM genres WHERE id = ?";
        List<Genre> genres = jdbc.query(sql, new GenreMapper(), id);
        return genres.stream().findFirst();
    }
}
