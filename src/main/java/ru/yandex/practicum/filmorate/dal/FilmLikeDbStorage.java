package ru.yandex.practicum.filmorate.dal;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashSet;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FilmLikeDbStorage {

    private final JdbcTemplate jdbc;

    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO film_likes (film_id, user_id, status) VALUES (?, ?, ?)";
        jdbc.update(sql, filmId, userId, "LIKE");
    }

    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
        jdbc.update(sql, filmId, userId);
    }

    public Set<Long> findLikesByFilmId(Long filmId) {
        String sql = "SELECT user_id FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbc.query(sql,
                (rs, rowNum) -> rs.getLong("user_id"),
                filmId));
    }

    public int countLikes(Long filmId) {
        String sql = "SELECT COUNT(*) FROM film_likes WHERE film_id = ?";
        return jdbc.queryForObject(sql, Integer.class, filmId);
    }
}