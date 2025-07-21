package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;

@Data
public class FilmLikeRequest {

    private Long userId;

    private Long filmId;

    private String status;
}
