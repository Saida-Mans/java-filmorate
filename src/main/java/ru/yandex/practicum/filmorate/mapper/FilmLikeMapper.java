package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dal.dto.FilmLikeRequest;
import ru.yandex.practicum.filmorate.model.FilmLike;

public class FilmLikeMapper {

    public static FilmLike mapToFilmLike(FilmLikeRequest request) {
        return new FilmLike(
                request.getUserId(),
                request.getFilmId(),
                request.getStatus()
        );
    }
}

