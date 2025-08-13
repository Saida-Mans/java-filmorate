package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dal.dto.GenreDto;
import ru.yandex.practicum.filmorate.model.Genre;

public class GenreRowMapper {
    public static GenreDto toDto(Genre genre) {
        return new GenreDto(genre.getId(), genre.getName());
    }

    public static Genre fromDto(GenreDto dto) {
        return new Genre(dto.getId(), dto.getName());
    }
}

