package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.mapper.RatingMapper;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RatingDbStorage {

    private final JdbcTemplate jdbc;

    public List<Rating> findAll() {
        String sql = "SELECT * FROM rating";
        return jdbc.query(sql, new RatingMapper());
    }

    public Optional<Rating> findById(Long id) {
        String sql = "SELECT * FROM rating WHERE id = ?";
        List<Rating> result = jdbc.query(sql, new RatingMapper(), id);
        return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
    }
}
