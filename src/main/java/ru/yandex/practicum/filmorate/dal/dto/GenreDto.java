package ru.yandex.practicum.filmorate.dal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenreDto {

    private Long id;

    private String name;
}
