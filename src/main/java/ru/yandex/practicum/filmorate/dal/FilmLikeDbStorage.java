package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

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

    public Map<Long, Set<Long>> loadLikesForFilms(Collection<Film> films) {
        if (films.isEmpty()) return Map.of();
        String inSql = films.stream()
                .map(f -> String.valueOf(f.getId()))
                .collect(Collectors.joining(","));
        String sql = String.format("""
        SELECT film_id, user_id
        FROM film_likes
        WHERE film_id IN (%s)
    """, inSql);

        Map<Long, Set<Long>> result = new HashMap<>();
        jdbc.query(sql, rs -> {
            long filmId = rs.getLong("film_id");
            long userId = rs.getLong("user_id");
            result.computeIfAbsent(filmId, k -> new HashSet<>()).add(userId);
        });
        return result;
    }
}