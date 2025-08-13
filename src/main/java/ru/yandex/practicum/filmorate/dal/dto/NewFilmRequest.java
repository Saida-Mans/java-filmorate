package ru.yandex.practicum.filmorate.dal.dto;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.Set;
import jakarta.validation.constraints.Size;

@Data
public class NewFilmRequest {

    private Long id;

    private String name;

    @Size(max = 200, message = "Описание не может быть длиннее 200 символов")
    private String description;

    private LocalDate releaseDate;

    private Long duration;

    private Set<Genre> genres;

    private MpaRequest mpa;

    @Data
    public static class MpaRequest {
        private Long id;
    }
}
