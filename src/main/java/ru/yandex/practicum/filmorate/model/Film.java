package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;

/**
 * Film.
 */
@Data
public class Film {
    Long id;
    String name;
    String description;
    Instant releaseDate;
    Long duration;
}
