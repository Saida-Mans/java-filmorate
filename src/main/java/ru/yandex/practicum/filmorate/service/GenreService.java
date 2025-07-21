package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.GenreDbStorage;
import ru.yandex.practicum.filmorate.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GenreService {
    private final GenreDbStorage genreStorage;

    public GenreService(GenreDbStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public List<GenreDto> getAllGenres() {
        return genreStorage.findAll().stream()
                .map(GenreRowMapper::toDto)
                .collect(Collectors.toList());
    }

    public Genre getGenreById(Long id) {
        return genreStorage.findById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
    }
}
