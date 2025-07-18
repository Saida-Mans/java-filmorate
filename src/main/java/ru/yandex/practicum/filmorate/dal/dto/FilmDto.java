package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data

public class FilmDto {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Long duration;
    private Set<Long> likes;
    private List<Genre> genres;
    private Rating mpa;
}

