package ru.yandex.practicum.filmorate.dal.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;

@Data
public class UpdateFilmRequest {

    private Long id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private Long duration;

    private Set<Genre> genres;

    private Long ratingId;

public boolean hasName() {
    return name != null && !name.isBlank();
}

public boolean hasDescription() {
    return description != null && !description.isBlank();
}

public boolean hasReleaseDate() {
    return releaseDate != null;
}

public boolean hasDuration() {
    return duration != null && duration > 0;
}

public boolean hasGenres() {
    return genres != null && !genres.isEmpty();
}

public boolean hasRating() {
    return ratingId != null;
}
}


